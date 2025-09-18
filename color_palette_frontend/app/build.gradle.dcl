androidApplication {
    namespace = "org.example.app"

    dependencies {
        // AndroidX core and appcompat for modern components and dark/light theme support
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

        // Testing - support both JUnit 5 and legacy JUnit 4 discovery
        implementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
        implementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
        implementation("junit:junit:4.13.2")
        implementation("androidx.test:core:1.5.0")
        implementation("androidx.test.ext:junit-ktx:1.1.5")
        implementation("androidx.test.espresso:espresso-core:3.5.1")

        // Keep example modules as required by template (not used by app)
        implementation(project(":utilities"))
        implementation("org.apache.commons:commons-text:1.11.0")
    }
}
