import org.gradle.kotlin.dsl.from

plugins {
    kotlin("jvm") version "2.1.0"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    signing
}

group = "com.skuralll"
version = "1.0.10"

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
    website.set("https://github.com/skuralll/mc_deploy")
    vcsUrl.set("https://github.com/skuralll/mc_deploy.git")
    plugins {
        create("mc_deploy") {
            id = "com.skuralll.mc_deploy"
            displayName = "mc_deploy"
            description = "deploy your minecraft plugin to server"
            tags.set(listOf("minecraft"))
            implementationClass = "com.skuralll.mc_deploy.MCDeployPlugin"
        }
    }
}

signing {
    useInMemoryPgpKeys(
        project.findProperty("signing.key") as String?,
        project.findProperty("signing.password") as String?
    )
    sign(publishing.publications)
}