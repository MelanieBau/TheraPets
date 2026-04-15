plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.therapets"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.therapets"
        minSdk = 27
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //FireBase

    //Gestión automática de las versiones de todos los paquetes de Firebase.
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))

    //Librería de autenticación de Firebase:
    //Crear usuarios nuevos con email y contraseña
    //Iniciar sesión con email y contraseña
    //Cerrar sesión
    //Recuperar contraseña por email
    implementation("com.google.firebase:firebase-auth")


    //Animación
    implementation("com.airbnb.android:lottie:6.3.0")
}