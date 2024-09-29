package com.skuralll.mc_deploy

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MCDeployPlugin : Plugin<Project> {

    companion object {
        // 設定ファイル名
        const val FILE_NAME = "mc_deploy.properties"
    }

    override fun apply(target: Project) {
        target.allprojects{
            project ->
            project.tasks.register("exTask") { task ->
                task.doLast {
                    // 設定ファイル読み込み
                    val localPropertiesFile = project.file(FILE_NAME)
                    if (!localPropertiesFile.exists()) {
                        throw GradleException("Task failed: $FILE_NAME file not found")
                    }
                }
            }
        }
    }
}