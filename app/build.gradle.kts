import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.translate.language.timelinemoduleonmap"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.translate.language.timelinemoduleonmap"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    2

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    /*kotlinOptions {
        jvmTarget = "1.8"
    }*/
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //size
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    //lottie
    implementation("com.airbnb.android:lottie:4.2.2")
    //location
    implementation("com.google.android.gms:play-services-location:20.0.0")
    //osm
    implementation("com.github.MKergall:osmbonuspack:6.9.0")

    //room db
//    /room db
    /* implementation("androidx.room:room-runtime:2.5.2")
     implementation("androidx.room:room-ktx:2.5.2")
     // To use Kotlin annotation processing tool (kapt)
     kapt("androidx.room:room-compiler:2.5.2")
     //ksp("androidx.room:room-compiler:2.5.2")
     */


    //room db
    implementation("androidx.room:room-runtime:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")

    //json
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
//    horizontal calaneder
    implementation ("io.github.b-sahana:horizontalcalendar:1.2.2")
//    implementation ("com.michalsvec:single-row-calednar:1.0.0")
//    implementation ("com.github.jhonnyx2012:horizontal-picker:1.0.6")
    implementation ("devs.mulham.horizontalcalendar:horizontalcalendar:1.3.4")


}