import org.gradle.api.JavaVersion

object Versions {
    const val android_gradle_plugin = "3.5.2"
    const val artifactory = "4.13.0"
    const val atsl_junit = "1.1.1"
    const val junit = "4.12"
    const val kotlin = "1.3.61"
    const val okhttp = "4.2.2"
    const val androidx_preference = "1.1.0"
    const val retrofit = "2.6.2"
    const val robolectric = "4.3.1"
    const val rx_android = "2.1.1"
    const val rxjava2 = "2.1.3"
    const val timber = "4.7.1"
}

object Config {
    const val minSdk = 19
    const val compileSdk = 29
    const val targetSdk = 29
    const val buildTools = "29.0.2"
    val javaVersion = JavaVersion.VERSION_1_8
}

object Tools {
    const val gradle = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val artifactory = "org.jfrog.buildinfo:build-info-extractor-gradle:${Versions.artifactory}"
}

object Libs {

    // Common
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

    // Rx
    const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rx_android}"

    // OkHttp
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_logging_intercepter = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofit_adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"

    // Utils
    const val androidx_preference_ktx = "androidx.preference:preference-ktx:${Versions.androidx_preference}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    // Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val androidx_junit = "androidx.test.ext:junit:${Versions.atsl_junit}"
    const val okhttp_mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
}