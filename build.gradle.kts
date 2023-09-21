import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.graalvm.buildtools.native") version "0.9.27"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
}

group = "dev.lutergs"
version = "0.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
//        jvmTarget = "21" // Java 21 is not yet supported by gradle
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// graalVM setting
graalvmNative {
    binaries.all {
        resources.autodetect()
    }
    toolchainDetection.set(false)
}

//graalvmNative {
//    binaries {
//        named("main") {
//            val javaVersion: String = System.getenv("JAVA_VERSION")
//            javaLauncher.set(javaToolchains.launcherFor {
//                languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
//                vendor.set(JvmVendorSpec.matching("Oracle"))
//            })
//        }
//    }
//}