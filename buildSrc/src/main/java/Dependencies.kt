import org.gradle.api.JavaVersion

object Versions {
    val android_gradle_plugin = "3.5.2"
    val artifactory = "4.13.0"
    val atsl_junit = "1.1.1"
    val junit = "4.12"
    val kotlin = "1.3.61"
    val okhttp = "4.2.2"
    val androidx_preference = "1.1.0"
    val retrofit = "2.6.2"
    val robolectric = "4.3.1"
    val rx_android = "2.1.1"
    val rxjava2 = "2.1.3"
    val timber = "4.7.1"
}

object Config {
    val minSdk = 19
    val compileSdk = 29
    val targetSdk = 29
    val buildTools = "29.0.2"
    val javaVersion = JavaVersion.VERSION_1_8
}

object Tools {
    val gradle = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val artifactory = "org.jfrog.buildinfo:build-info-extractor-gradle:${Versions.artifactory}"
}

object Libs {

    // Common
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

    // Rx
    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rx_android}"

    // OkHttp
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    val okhttp_logging_intercepter = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Retrofit
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"

    // Utils
    val androidx_preference_ktx = "androidx.preference:preference-ktx:${Versions.androidx_preference}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    // Testing
    val junit = "junit:junit:${Versions.junit}"
    val androidx_junit = "androidx.test.ext:junit:${Versions.atsl_junit}"
    val okhttp_mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
}