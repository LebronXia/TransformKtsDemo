package com.pience.gradlektsdemo.com.xiamu.transform.thread

import com.xiamu.transform.thread.MoonTryCatchMethodVisitorJava
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

class TryCatchClassVisitor : ClassVisitor {
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
        //方法过滤
//        if (!"strToNumber2".equals(name))
//            return super.visitMethod(access, name, descriptor, signature, exceptions)

        var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return IMEIMethodAdapter(api, methodVisitor, access, name, descriptor, this.className)
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}