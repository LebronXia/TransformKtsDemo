package com.pience.gradlektsdemo.com.xiamu.transform
import com.xiamu.transform.base.BaseTransform
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

/**
 * Created by zxb in 2022/7/27
 */
class MoonTransform : BaseTransform() {

    override fun modifyClass(byteArray: ByteArray): ByteArray {

//        var classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
//        val classVisitor = MoonClassAdapter(Opcodes.ASM5, classWriter)
//        val classReader= ClassReader(byteArray)
//        //调用 ClassReader.accept()方法完成对class遍历，并把相关信息记录到 ClassNode 对象中
//        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return byteArray
    }

    override fun getName(): String {
        return "MoonTransform"
    }


//    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
//        return TransformManager.CONTENT_CLASS
//    }
//
//    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
//        return mutableSetOf(
//            QualifiedContent.Scope.PROJECT,
//            QualifiedContent.Scope.SUB_PROJECTS,
////            QualifiedContent.Scope.EXTERNAL_LIBRARIES
//        )
//    }
}