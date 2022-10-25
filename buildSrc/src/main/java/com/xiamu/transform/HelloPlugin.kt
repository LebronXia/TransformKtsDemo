package com.pience.gradlektsdemo.com.xiamu.transform

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Created by zxb in 2022/2/26
 */
class HelloPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        println("MyPlugin执行了")
        target.task("mytask") {
            doLast {
                println("MyPlugin中的task执行了")
            }
        }

//        val appExtension: AppExtension = target.extensions.getByType()
//        appExtension?.registerTransform(HelloTransform())
    }
}