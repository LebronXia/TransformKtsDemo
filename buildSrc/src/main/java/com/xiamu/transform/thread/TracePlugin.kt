package com.pience.gradlektsdemo.com.xiamu.transform.thread

import com.android.build.gradle.AppExtension
import com.pience.gradlektsdemo.com.xiamu.transform.MoonTransform
import com.pience.gradlektsdemo.com.xiamu.transform.privacy.PrivacySentryPlugin
import com.xiamu.transform.privacy.PrivacySentryConfig
import com.xiamu.transform.privacy.PrivacySentryGradleConfig
import com.xiamu.transform.privacy.PrivacySentryTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import com.xiamu.transform.utils.Log

/**
 * Created by zxb in 2022/7/28
 */
class TracePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        //println("TracePlugin执行了")
        Log.log("TracePlugin执行了")
//        val appExtension: AppExtension = project.extensions.getByType()
//        appExtension.registerTransform(TraceTransform())


        //配置后调用
//        project.afterEvaluate{
//            val appExtension: AppExtension = project.extensions.getByType()
//            appExtension.registerTransform(
//                TraceTransform()
//            )
//        }

        val appExtension: AppExtension = project.extensions.getByType()
        appExtension.registerTransform(TraceTransform())
    }
}