plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kirill1636.chessmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kirill1636.chessmate"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        addManifestPlaceholders(mapOf(
            "VKIDRedirectHost" to "vk.com", // обычно vk.com
            "VKIDRedirectScheme" to "vk51843076", // обычно vk{ID приложения}
            "VKIDClientID" to "51843076",
            "VKIDClientSecret" to "QawCmZcooPac0ExJThU8"
        ))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        // For AGP 4.1+
        isCoreLibraryDesugaringEnabled = true
        // For AGP 4.0
        // coreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/notice.txt"
        }


    }
    buildToolsVersion = "34.0.0"
}

val sdkVersion = "1.2.0"

dependencies {
    // Если используете авторизацию без UI, укажите эту зависимость.
    implementation("com.vk.id:vkid:${sdkVersion}")
    // Если используете One Tap на Compose, укажите эту зависимость.
    implementation("com.vk.id:onetap-compose:${sdkVersion}")
    // Если используете One Tap на XML, укажите эту зависимость.
    implementation("com.vk.id:onetap-xml:${sdkVersion}")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("androidx.compose.runtime:runtime:1.5.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment:2.5.1")
    implementation("androidx.navigation:navigation-ui:2.5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.springframework.android:spring-android-rest-template:1.0.1.RELEASE")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.3.2")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.9")

}