/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

import org.gradle.api.JavaVersion

object Versions {
    val android_gradle_plugin = "3.5.2"
    val arch_core = "2.1.0"
    val artifactory = "4.15.2"
    val atsl_junit = "1.1.1"
    val junit = "4.13"
    val kotlin = "1.4.0"
    val mockito = "3.5.0"
    val okhttp = "4.8.1"
    val preference = "1.1.1"
    val retrofit = "2.9.0"
    val robolectric = "4.3.1"
    val runner = "1.1.0"
    val rx_android = "2.1.1"
    val rxjava2 = "2.2.19"
    val timber = "4.7.1"
}

object Config {
    val buildTools = "29.0.2"
    val compileSdk = 29
    val minSdk = 19
    val targetSdk = 29
    val javaVersion = JavaVersion.VERSION_1_8
}

object Tools {
    val artifactory =
        "org.jfrog.buildinfo:build-info-extractor-gradle:${Versions.artifactory}"
    val gradle = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Libs {

    // Common
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

    // Rx
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rx_android}"
    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"

    // OkHttp
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    val okhttp_logging_interceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Retrofit
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_adapter_rxjava2 =
        "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    // Utils
    val androidx_preference_ktx = "androidx.preference:preference-ktx:${Versions.preference}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    // Testing
    val androidx_core_testing = "androidx.arch.core:core-testing:${Versions.arch_core}"
    val androidx_junit = "androidx.test.ext:junit:${Versions.atsl_junit}"
    val androidx_runner = "androidx.test:runner:${Versions.runner}"
    val junit = "junit:junit:${Versions.junit}"
    val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    val okhttp_mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
}