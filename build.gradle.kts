import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
//    id("org.graalvm.buildtools.native") version "0.9.27"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
}

group = "dev.lutergs"
version = "0.3.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
    testImplementation("io.projectreactor:reactor-test:3.5.4")
    testImplementation("org.springframework.batch:spring-batch-test:5.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21" // Java 21 is not yet supported by gradle
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
