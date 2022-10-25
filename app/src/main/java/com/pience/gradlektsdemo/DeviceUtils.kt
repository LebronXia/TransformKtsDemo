package com.pience.gradlektsdemo

import android.app.Service
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Created by zxb in 2022/3/4
 */
object DeviceUtils {
    fun getDeviceId(context: Context): String {
        return try {
            val telephonyManager =
                context.getSystemService(Service.TELEPHONY_SERVICE) as? TelephonyManager
            telephonyManager?.deviceId ?: ""
        } catch (e: Throwable) {
            e.printStackTrace()
            ""
        }
    }

    fun getBrand(): String {
        return Build.BRAND
    }

//    private fun writeToFile(log: String, throwable: Throwable){
//        var byteArrayOutStream = ByteArrayOutputStream()
//        throwable.printStackTrace(PrintStream(byteArrayOutStream))
//        var stackTrace = byteArrayOutStream.toString()
//        val realLog = log + stackTrace
//        PrivacySentryRecord.writeToFile(realLog)
//    }

}