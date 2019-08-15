/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ClassName", "unused")

object Build {
    const val applicationId = "com.ivianuu.compose.sample"
    const val buildToolsVersion = "28.0.3"

    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val targetSdkSample = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Publishing {
    const val groupId = "com.ivianuu.compose"
    const val vcsUrl = "https://github.com/IVIanuu/compose"
    const val version = "${Build.versionName}-dev20"
}

object Versions {
    const val androidGradlePlugin = "3.5.0-rc01"

    const val androidxAppCompat = "1.1.0-rc01"
    const val androidxUi = "1.0.0-alpha01"
    const val androidxRecyclerView = "1.1.0-beta01"

    const val bintray = "1.8.4"

    const val coroutines = "1.3.0-RC"

    const val kotlin = "1.3.41"

    const val leakCanary = "2.0-beta-2"

    const val mavenGradle = "2.1"

    const val materialComponents = "1.1.0-alpha07"

    const val roboelectric = "4.0.2"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxRecyclerView =
        "androidx.recyclerview:recyclerview:${Versions.androidxRecyclerView}"
    const val androidxUiCore = "androidx.ui:ui-core:${Versions.androidxUi}"

    const val bintrayGradlePlugin =
        "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}"

    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val leakCanaryAndroid =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val mavenGradlePlugin =
        "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"
}