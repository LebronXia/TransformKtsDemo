import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.31"
}

gradlePlugin {
    plugins {

        create("helloplugin") {
            id = "hello-plugin"
            implementationClass = "com.pience.gradlektsdemo.com.xiamu.transform.HelloPlugin"
            description = "测试插件"
        }

        create("PrivacySentryPlugin"){
            id = "privacysentry-plugin"
            implementationClass = "com.pience.gradlektsdemo.com.xiamu.transform.privacy.PrivacySentryPlugin"
            description = "测试插件"
        }

        create("moonPlugin"){
            id = "moon-plugin"
            implementationClass = "com.pience.gradlektsdemo.com.xiamu.transform.MoonPlugin"
            description = "耗时插件"
        }

        create("tracePlugin"){
            id = "trace-plugin"
            implementationClass = "com.pience.gradlektsdemo.com.xiamu.transform.thread.TracePlugin"
            description = "全局修改线程名插件"
        }
    }
}


val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

repositories {
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.2")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.2")
    compileOnly("org.ow2.asm:asm-tree:9.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.21")
}
