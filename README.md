# android-QMobileAPI

[![Language](http://img.shields.io/badge/language-kotlin-purple.svg?style=flat)](https://developer.android.com/kotlin)
[![Build](https://github.com/4d/android-QMobileAPI/actions/workflows/build.yml/badge.svg)](https://github.com/4d/android-QMobileAPI/actions/workflows/build.yml)

Network layer to communicate with 4D server rest API. Part of [Android SDK](https://github.com/4d/android-SDK)

## Build

To build you can open project with Android Studio or run

```bash
./gradlew assemble --console=rich
```

To check also your development environment you can run instead [`build.sh`](build.sh)

### Requirements

By installing [Android Studio](https://developer.android.com/) you could fulfill all requirements, but you could also download them separately.

#### Java 11

You could download it [here](https://www.oracle.com/java/technologies/downloads/)

💡 Java 11 is embedded in recent Android Studio versions. For instance on macOS you could do 

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

#### Android SDK

To build you should download Android SDK or use one provided by Android Studio.

Then `ANDROID_HOME` environnement variable must defined the SDK path.

```bash
export ANDROID_HOME=<path to sdk>
```

Default path when using Android Studio for Android SDK on macOS is `$HOME/Library/Android/sdk` and on linux `$HOME/Android/Sdk`

## Dependencies

| Name | License | Usefulness |
|-|-|-|
| [Retrofit](https://github.com/square/retrofit) | [Apache 2.0](https://github.com/square/retrofit/blob/master/LICENSE.txt) | Type-safe HTTP client |
| [OkHttp](https://github.com/square/okhttp) | [Apache 2.0](https://github.com/square/okhttp/blob/master/LICENSE.txt) | HTTP client |
