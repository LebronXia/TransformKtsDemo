plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("trace-plugin")
}


android {
    val sdkVersion: Int by rootProject.extra
    compileSdk = sdkVersion

    defaultConfig {
        applicationId = "com.pience.gradlektsdemo"
        minSdk = 23
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
//        getByName("release"){
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
//            )
//        }

        val release by getting {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        val beta by creating {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
   // implementation("com.android.support:appcompat-v7:${rootProject.extra["supportLibVersion"]}")
    implementation(com.pience.gradlektsdemo.deps.kotlin.coretx)
    implementation(com.pience.gradlektsdemo.deps.android.support.compat)
    implementation(com.pience.gradlektsdemo.deps.android.support.material)
    implementation(com.pience.gradlektsdemo.deps.android.support.constraintLayout)
    testImplementation(com.pience.gradlektsdemo.deps.android.test.junit)
    androidTestImplementation(com.pience.gradlektsdemo.deps.android.test.runner)
    androidTestImplementation(com.pience.gradlektsdemo.deps.android.test.espressoCore)
    implementation("commons-io:commons-io:2.6")

//    id("hello-plugin")
//    id("privacysentry-plugin")
}

