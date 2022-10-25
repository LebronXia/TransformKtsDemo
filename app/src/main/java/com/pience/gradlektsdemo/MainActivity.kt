package com.pience.gradlektsdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
       // var startTime = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        System.out.println(System.currentTimeMillis() - startTime)
//        var endTime = System.currentTimeMillis() - startTime
//        var sb = StringBuilder()
//        sb.append("com/sample/asm/SampleApplication.onCreate time: ")
//        sb.append(endTime)
//        Log.d("MethodCostTime", sb.toString())

        startThread()
    }


    fun startThread() {
        Thread({
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, "test").start()
    }
}