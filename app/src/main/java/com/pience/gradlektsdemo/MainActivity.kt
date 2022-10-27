package com.pience.gradlektsdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var currentTimeMillis = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       val currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis
        Log.d("Geek", "com/pience/gradlektsdemo/MainActivity.startThread2 time:$currentTimeMillis2")

//         startThread()
//         point("org.itstack.test.MethodTest.strToNumber", 2)

        // strToNumber1("10")
//        strToNumber2("10")
//        getIMEI()
    }

//    fun strToNumber1(str: String) : Int{
//        try {
//            var item = str.toInt()
//            return item
//        } catch (e: Exception){
//            throw e
//        }
//    }

    fun strToNumber2(str: String) : Int{
        point("org.itstack.test.MethodTest.strToNumber", 2)
        return str.toInt()
    }


    fun startThread() {
        val currentTime = System.currentTimeMillis()
        Thread({
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, "test").start()
        println(System.currentTimeMillis() - currentTime);
    }

    fun startThread2() {
        Thread({
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, "test").start()
    }

//    fun startThread2() {
//        val currentTimeMillis = System.currentTimeMillis()
//        CustomThread(`MainActivity$$ExternalSyntheticLambda0`.INSTANCE, "test").start()
//        val currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis
//        Log.d(
//            "Geek",
//            "com/pience/gradlektsdemo/MainActivity.startThread2 time:$currentTimeMillis2"
//        )
//    }

    fun point(methodName: String, response: Any?) {
        println(
            """系统监控 :: [方法名称：$methodName 输出信息]
"""
        )
    }

    fun getIMEI(): String? {
        val telManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return ""
        }
        return if (telManager == null) "null" else telManager.deviceId
    }
}