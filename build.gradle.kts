import org.gradle.kotlin.dsl.from

plugins {
    kotlin("jvm") version "2.1.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.skuralll"
version = "1.0.9"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.hierynomus:sshj:0.38.0")
    implementation("nl.vv32.rcon:rcon:1.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("mc_deploy") {
            id = "com.skuralll.mc_deploy"
            implementationClass = "com.skuralll.mc_deploy.MCDeployPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io") // JitPack を指定
        }
    }
}