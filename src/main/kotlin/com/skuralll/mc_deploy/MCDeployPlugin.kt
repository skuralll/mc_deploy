package com.skuralll.mc_deploy

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

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
                    val propertiesFile = project.file(FILE_NAME)
                    val properties = Properties()
                    if(!propertiesFile.exists()){
                        throw GradleException("Task failed: $FILE_NAME file not found")
                    }
                    properties.load(propertiesFile.inputStream())
                    // TODO : SSH接続
                }
            }
        }
    }
}