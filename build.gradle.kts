plugins {
    kotlin("jvm") version "1.9.22"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.skuralll"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.hierynomus:sshj:0.38.0")
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