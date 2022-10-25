package com.pience.gradlektsdemo.com.xiamu.transform

import com.android.build.gradle.AppExtension
import com.pience.gradlektsdemo.com.xiamu.transform.privacy.PrivacySentryPlugin
import com.xiamu.transform.privacy.PrivacySentryConfig
import com.xiamu.transform.privacy.PrivacySentryGradleConfig
import com.xiamu.transform.privacy.PrivacySentryTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Created by zxb in 2022/7/27
 */
class MoonPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        println("MoonPlugin执行了")
        val appExtension: AppExtension = project.extensions.getByType()
        appExtension.registerTransform(MoonTransform())
    }

}