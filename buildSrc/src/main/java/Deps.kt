package com.pience.gradlektsdemo

/**
 * Created by zxb in 2022/2/18
 */
object deps {
    object plugin {
        val gradle = "com.android.tools.build:gradle:7.0.2"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20"
    }

    object kotlin {
        val coretx = "androidx.core:core-ktx:1.3.2"
    }

    object android {
        object support {
            val compat = "androidx.appcompat:appcompat:1.2.0"
            val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
            val material = "com.google.android.material:material:1.3.0"
        }

        object test {
            val junit = "junit:junit:4.+"
            val runner = "androidx.test.ext:junit:1.1.2"
            val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }
}