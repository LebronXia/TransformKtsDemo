package com.pience.gradlektsdemo.com.xiamu.transform.thread

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

//根据注解添加耗时统计
//https://www.jianshu.com/p/0116d610e430
class CostTimeClassAdapter : ClassVisitor {
    constructor(api: Int, classVisitor: ClassVisitor?) : super(api, classVisitor)

    private var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    inner class CostTimeMethodAdapter: AdviceAdapter{

        private var isAnnotationed = false

        constructor(
            api: Int,
            methodVisitor: MethodVisitor?,
            access: Int,
            name: String?,
            descriptor: String?,
            className: String
        ) : super(api, methodVisitor, access, name, descriptor) {

        }

        //遍历代码的开始 声明一个局部变量
        override fun visitCode() {
            super.visitCode()
        }

        override fun visitFieldInsn(
            opcode: Int,
            owner: String?,
            name: String?,
            descriptor: String?
        ) {
            super.visitFieldInsn(opcode, owner, name, descriptor)
        }

        override fun visitIntInsn(opcode: Int, operand: Int) {
            super.visitIntInsn(opcode, operand)
        }

        //遍历操作码  判断是否是return语句
        override fun visitInsn(opcode: Int) {

            if (opcode >= IRETURN && opcode <= RETURN || opcode == ATHROW){

            }
            super.visitInsn(opcode)
        }

        //遍历注解
        override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
            isAnnotationed = ("Lcom/dsg/annotations/CostTime;".equals(descriptor));

            return super.visitAnnotation(descriptor, visible)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack, maxLocals)
        }

        override fun visitEnd() {
            super.visitEnd()
        }



    }


}