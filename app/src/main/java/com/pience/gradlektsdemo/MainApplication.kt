package com.pience.gradlektsdemo

import android.app.Application

/**
 * @Author: leavesCZY
 * @Date: 2021/12/25 15:28
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainApplication : Application() {

    companion object {
        lateinit var application: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        var startTime = System.currentTimeMillis()


    }

}