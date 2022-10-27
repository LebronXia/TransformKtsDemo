package com.pience.gradlektsdemo.com.xiamu.transform.thread

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import com.xiamu.transform.utils.Log

//https://blog.csdn.net/qq_17766199/article/details/88019688
class IMEIMethodAdapter : AdviceAdapter{

    private lateinit var methodName: String
    private lateinit var className: String
    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        className: String
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.methodName = name!!
        this.className = className
    }

    //处理方法调用
    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)

        if (owner.equals("android/telephony/TelephonyManager") && name.equals("getDeviceId") && descriptor.equals("()Ljava/lang/String;")){
            Log.log("get imei className:" + className + ",method:" + methodName + ", name:" + name);

            //Log.e("asmcode", "get imei className:%s, method:%s, name:%s", className, methodName, name);
        }

    }
}