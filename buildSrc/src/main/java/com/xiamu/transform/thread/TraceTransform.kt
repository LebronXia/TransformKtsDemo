package com.pience.gradlektsdemo.com.xiamu.transform.thread

import com.xiamu.transform.base.BaseTransform2
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * Created by zxb in 2022/7/28
 */
class TraceTransform : BaseTransform2() {

    override fun modifyClass(byteArray: ByteArray): ByteArray {
        val classReader = ClassReader(byteArray)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

        val cv = TraceClassVisitor(Opcodes.ASM6, classWriter)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
       // return byteArray
    }
}