plugins {
    kotlin("jvm") version "1.4.32"
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.nononsensecode"
version = "0.0.1.RELEASE"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Logging
    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.microutils:kotlin-logging:1.6.25")
    implementation("org.codehaus.groovy:groovy:3.0.3")
}

extra["executeProfile"] = {
    val buildProfile: String? by project
    val profileFileRelativePath = "profiles/profile-${buildProfile?:"development"}.gradle.kts"
    file(".").absoluteFile.resolve(profileFileRelativePath).absolutePath
}

extra["getConfig"] = {
    val configFileRelativePath = "profiles/common.gradle.kts"
    file(".").absoluteFile.resolve(configFileRelativePath).absolutePath
}

val executeProfile: () -> String by extra
apply(from = executeProfile())