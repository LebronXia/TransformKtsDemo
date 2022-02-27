package com.xiamu.transform.utils

import org.apache.commons.codec.binary.Hex
import java.io.File

/**
 * Created by zxb in 2022/2/25
 */
object DigestUtils {
    fun generateJarFileName(jarFile: File): String {
        return getMd5ByFilePath(jarFile) + "_" + jarFile.name
    }

    fun generateClassFileName(classFile: File): String {
        // 重名名输出文件,因为可能同名,会覆盖
        return getMd5ByFilePath(classFile) + "_" + classFile.name
    }

    private fun getMd5ByFilePath(file: File): String {
        return Hex.encodeHexString(file.absolutePath.toByteArray()).substring(0, 8)
    }
}