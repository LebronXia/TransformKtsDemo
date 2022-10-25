package com.pience.gradlektsdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_privacysentry.*

/**
 * Created by zxb in 2022/3/4
 */
class PrivacySentryActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacysentry)

        btnGetDeviceId.setOnClickListener {
            DeviceUtils.getDeviceId(this)
        }

        btnGetDeviceBrand.setOnClickListener {
            DeviceUtils.getBrand()
        }
    }
}