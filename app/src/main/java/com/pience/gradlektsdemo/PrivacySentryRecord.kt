package com.pience.gradlektsdemo

import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zxb in 2022/3/4
 */
object PrivacySentryRecord {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA)

    val logFile = File(MainApplication.application.externalCacheDir, "PrivacySentry.txt")

    @JvmStatic
    fun writeToFile(log: String) {
        val time = simpleDateFormat.format(Date(System.currentTimeMillis()))
        val logFormat = "\n" + time + "\n" + log
        FileUtils.write(
            logFile,
            logFormat,
            Charset.defaultCharset(),
            true
        )
    }
}