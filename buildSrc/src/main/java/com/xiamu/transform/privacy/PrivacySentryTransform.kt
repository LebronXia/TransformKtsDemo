package com.xiamu.transform.privacy

import com.xiamu.transform.base.BaseTransform
import java.lang.RuntimeException
import com.xiamu.transform.utils.Log
import org.apache.commons.io.FileUtils
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ASTORE
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.filechooser.FileSystemView
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * Created by zxb in 2022/3/4
 */
class PrivacySentryTransform(private val config: PrivacySentryConfig): BaseTransform() {

    companion object {
        private const val writeToFileMethodName = "writeToFile"
        private const val writeToFileMethodDesc = "(Ljava/lang/String;Ljava/lang/Throwable;)V"
    }

    private var runtimeRecord: PrivacySentryRuntimeRecord ?= null

    private val allLintLog = StringBuffer()

    override fun onTransformStart() {
        runtimeRecord = PrivacySentryConfig.runtimeRecord
    }

    override fun modifyClass(byteArray: ByteArray): ByteArray {
        val classNode = ClassNode()
        val classReader= ClassReader(byteArray)
        //调用 ClassReader.accept()方法完成对class遍历，并把相关信息记录到 ClassNode 对象中
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES)

        val methods = classNode.methods
        val mRuntimeRecord = runtimeRecord
        if (!methods.isNullOrEmpty()){
            val taskList = mutableListOf<() -> Unit>()

            val tempLintLog = StringBuilder()
            for(methodNode in methods){

                //操作码列表
                val instructions = methodNode.instructions
                //遍历
                val instructionIterator = instructions?.iterator()

                if (instructionIterator != null){
                    //遍历操作列表
                    while (instructionIterator.hasNext()){
                        //指定操作码
                        val instruction = instructionIterator.next()
                        //发现getDeviced节点，是否有匹配
                        if (instruction.isHookPoint()){
                            val lintLog = getLintLog(classNode, methodNode, instruction)

                            tempLintLog.append(lintLog)
                            tempLintLog.append("\n\n")
                            if (mRuntimeRecord != null){

                                taskList.add{
                                    //在instruction指令之前，插入调用wroteToFile的指令
                                    insertRuntimeLog(classNode, methodNode, instruction)
                                 }
                            }

                            Log.log("PrivacySentryTransfoor $lintLog")
                        }
                    }
                }
            }

            //是否有记录
            if (tempLintLog.isNotBlank()){
                allLintLog.append(tempLintLog)
            }

            if (taskList.isNotEmpty() && mRuntimeRecord != null){
                taskList.forEach {
                    it.invoke()
                }

                val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                classNode.accept(classWriter)
                generateWriteToFileMethod(classWriter, mRuntimeRecord)
                return classWriter.toByteArray()
            }
        }


        return byteArray
    }

    //通过对比签名信息来判断是否属于目标指令
    private fun AbstractInsnNode.isHookPoint():Boolean{
        when(this){
            //寻找调用TelephonyManager
            is MethodInsnNode -> {
                val owner = this.owner
                val desc = this.desc
                val name = this.name
                val find = config.methodHookPoint.find {
                    it.owner == owner && it.desc == desc && it.name == name
                }
                return find != null
            }

            is FieldInsnNode -> {
                val owner = this.owner
                val desc = this.desc
                val name = this.name
                val find = config.fieldHookPointList.find {
                    it.owner == owner && it.desc == desc && it.name == name
                }
                return find != null
            }
        }
        return false
    }

    //将操作所在的类路径，调用者的方法签名。隐私操作拼接
    /**
     * ClassVisitor 的一个子类
     * MethodVisitor 的一个子类 操作的字节码
     * hokeInstruction ： 操作码节点
     */
    private fun getLintLog(classNode: ClassNode,
                           methodNode: MethodNode,
                           hokeInstruction: AbstractInsnNode): StringBuilder{
        val classPath = classNode.name
        val methodName = methodNode.name
        val methodDesc = methodNode.desc
        //owner 所在的类 name 名称  desc 字段类型
        val owner : String
        val desc: String
        val name: String

        when(hokeInstruction){
            //方法调用操作的字节码
            is MethodInsnNode -> {
                owner = hokeInstruction.owner
                desc = hokeInstruction.desc
                name = hokeInstruction.name
            }
            //字段操作的字节码
            is FieldInsnNode -> {
                owner = hokeInstruction.owner
                desc = hokeInstruction.desc
                name = hokeInstruction.name
            }
            else -> {
                throw  RuntimeException("非法指令")
            }
        }

        val lintLog = java.lang.StringBuilder()
        lintLog.append(classPath)
        lintLog.append(" -> ")
        lintLog.append(methodName)
        lintLog.append(" -> ")
        lintLog.append(methodDesc)
        lintLog.append("\n")
        lintLog.append(owner)
        lintLog.append(" -> ")
        lintLog.append(name)
        lintLog.append(" -> ")
        lintLog.append(desc)
        return lintLog
    }

    //
    private fun insertRuntimeLog(
        classNode: ClassNode,
        methodNode: MethodNode,
        hokeInstruction: AbstractInsnNode){

        //操作码列表类型
        val insnList = InsnList()
        insnList.apply {
            //拼接
            val lintLog = getLintLog(classNode, methodNode, hokeInstruction)
            lintLog.append("\n")
             (LdcInsnNode(lintLog.toString()))
            add(TypeInsnNode(Opcodes.NEW, "java/lang/Throwable"))
            //一切无参数值操作的字节码
            add(InsnNode(Opcodes.DUP))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable",
            "<init>", "()V", false
            ))
            //插入一个方法调用指令
            add(MethodInsnNode(
                Opcodes.INVOKESPECIAL, classNode.name,
                writeToFileMethodName,
                writeToFileMethodDesc
            ))
        }

        //插入到getBrand之前
        methodNode.instructions.insertBefore(hokeInstruction, insnList)
    }

    private fun generateWriteToFileMethod(classWriter: ClassWriter,
                                          runtimeRecord: PrivacySentryRuntimeRecord){
        val methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC,
            writeToFileMethodName, writeToFileMethodDesc,
            null, null
        )
        methodVisitor.visitCode()
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/io/ByteArrayOutputStream")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
            "java/io/ByteArrayOutputStream", "<init>" ,"()V", false
        )
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitTypeInsn(Opcodes.NEW, "ava/io/PrintStream")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitVarInsn(ALOAD, 2);


        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/PrintStream", "<init>", "(Ljava/io/OutputStream;)V", false);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "printStackTrace", "(Ljava/io/PrintStream;)V", false);

        methodVisitor.visitMethodInsn(
             Opcodes.INVOKEVIRTUAL,
            "java/io/ByteArrayOutputStream",
            "toString",
            "()Ljava/lang/String;",
            false
        )

        methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ASTORE, 3)
        methodVisitor.visitTypeInsn(org.objectweb.asm.Opcodes.NEW, "java/lang/StringBuilder")
        methodVisitor.visitInsn(org.objectweb.asm.Opcodes.DUP)
        methodVisitor.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKESPECIAL,
            "java/lang/StringBuilder",
            "<init>",
            "()V",
            false
        )
        methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 3)
        methodVisitor.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        methodVisitor.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )
        methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ASTORE, 4)
        methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 4)
        methodVisitor.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKESTATIC,
            runtimeRecord.methodOwner,
            runtimeRecord.methodName,
            runtimeRecord.methodDesc,
            false
        )
        methodVisitor.visitInsn(org.objectweb.asm.Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 5)
        methodVisitor.visitEnd()
    }


    override fun onTransformEnd() {
        super.onTransformEnd()
        if (allLintLog.isNotEmpty()) {
            FileUtils.write(generateLogFile(), allLintLog, Charset.defaultCharset())
        }
        runtimeRecord = null
    }


    private fun generateLogFile(): File {
        val time = SimpleDateFormat(
            "yyyy_MM_dd_HH_mm_ss",
            Locale.CHINA
        ).format(Date(System.currentTimeMillis()))
        return File(
            FileSystemView.getFileSystemView().homeDirectory,
            "PrivacySentry_${time}.txt"
        )
    }

}