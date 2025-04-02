plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    //id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.purang.hellofood"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.purang.hellofood"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //hiltViewModel
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.52")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.0") // 최신 버전으로 수정
    kapt("com.google.dagger:hilt-compiler:2.52")

    //livedata
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")

    //retrofit2
    // retrofit2 (http)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    //coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    //permission accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.37.0")

    //구글로그인
    implementation ("com.google.android.gms:play-services-auth:20.4.1")

    val credential = "1.5.0" // 안정화 버전
    val googleId = "1.1.1"
    implementation("androidx.credentials:credentials:$credential")
    implementation("androidx.credentials:credentials-play-services-auth:$credential")
    implementation("com.google.android.libraries.identity.googleid:googleid:$googleId")

    //데이터 스토어
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //firebase
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    //calendar
    implementation ("com.github.uuranus:schedule-calendar-compose:1.0.3")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.default.properties"
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")
}