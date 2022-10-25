// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript{

    extra["sdkVersion"] = 31
    extra["supportLibVersion"] = "28.0.0"

    repositories(){
        google()
        mavenCentral()
    } 


    dependencies{
        classpath (com.pience.gradlektsdemo.deps.plugin.gradle)
        classpath (com.pience.gradlektsdemo.deps.plugin.kotlin)
    }
}

tasks{
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}

task("add"){
    println("config")
    this.doFirst{
        println("doFirst")
    }

    doLast {
        val num1 = 10
        val num2 = 20
        println("name is ${ext["name"]},")
    }
}

ext {
    set("name", "zxnnnn")
    set("kotlinVersion", "1.5.20")
}
