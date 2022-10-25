package com.pience.gradlektsdemo.com.xiamu.transform.privacy

import com.android.build.gradle.AppExtension
import com.xiamu.transform.privacy.PrivacySentryConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.xiamu.transform.privacy.PrivacySentryGradleConfig
import com.xiamu.transform.privacy.PrivacySentryTransform
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

/**
 * Created by zxb in 2022/3/21
 */
class PrivacySentryPlugin: Plugin<Project> {

    companion object{
        private const val EXT_NAME= "PrivacySentry"
    }

    override fun apply(project: Project) {
        project.extensions.create<PrivacySentryGradleConfig>(EXT_NAME)
        project.afterEvaluate{
            val config = (extensions.findByName(EXT_NAME) as? PrivacySentryGradleConfig)
                ?: PrivacySentryGradleConfig()


            val appExtension: AppExtension = project.extensions.getByType()
            appExtension.registerTransform(
                PrivacySentryTransform(
                    config = PrivacySentryConfig()
                )
            )

        }
    }
}