import org.gradle.kotlin.dsl.from

plugins {
    kotlin("jvm") version "2.1.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.skuralll"
version = "1.0.7"

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
            name = "gpr"
            url = uri("https://maven.pkg.github.com/skuralll/mc_deploy")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}