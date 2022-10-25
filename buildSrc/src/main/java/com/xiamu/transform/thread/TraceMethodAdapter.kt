package com.pience.gradlektsdemo.com.xiamu.transform.thread

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter
import com.xiamu.transform.utils.Log

/**
 * 替换名字
 * Created by zxb in 2022/7/28
 */
//类访问者
class TraceClassVisitor : ClassVisitor{
    constructor(api: Int, classVisitor: ClassVisitor?) : super(api, classVisitor)

    private lateinit var className: String
    //访问类的描述信息
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

        var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return TraceMethodAdapter(api, methodVisitor, access, name, descriptor, this.className)
    }

    override fun visitEnd() {
        super.visitEnd()
    }


    // AdviceAdapter 也是 MethodVisitor 的子类，不同于 MethodVisitor的是，
    // 它自身提供了 onMethodEnter 与 onMethodExit 方法，非常便于我们去实现方法的前后插桩
    inner class TraceMethodAdapter: AdviceAdapter {

        private lateinit var methodName: String
        private lateinit var className: String
        private var find = false

        constructor(
            api: Int,
            methodVisitor: MethodVisitor?,
            access: Int,
            name: String?,
            descriptor: String?,
            className: String
        ) : super(api, methodVisitor, access, name, descriptor) {
            this.className = className
            this.methodName = name!!
        }

        //类型调用
        override fun visitTypeInsn(opcode: Int, type: String?) {
            //
            if (opcode == Opcodes.NEW && "java/lang/Thread" == type){
                find = true
                mv.visitTypeInsn(Opcodes.NEW, "com/pience/gradlektsdemo/CustomThread")
                return
            }
            super.visitTypeInsn(opcode, type)
        }

        /**
         * 处理方法调用
         * @param opcode
         * @param owner 方法调用的类
         * @param methodName 方法名称
         * @param desc 方法参数及返回值
         * @param b
         */
        override fun visitMethodInsn(
            opcodeAndSource: Int,
            owner: String?,
            name: String?,
            descriptor: String?,
            isInterface: Boolean
        ) {
            if ("java/lang/Thread" == owner && className != "com/pience/gradlektsdemo/CustomThread"
                && opcodeAndSource == Opcodes.INVOKESPECIAL && find){
                find = false
                mv.visitMethodInsn(opcodeAndSource, "com/pience/gradlektsdemo/CustomThread", name, descriptor, isInterface)
                Log.log( "className:" + className +  "method:" + "name:"+ name )
                return
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)

        }

    }

}
