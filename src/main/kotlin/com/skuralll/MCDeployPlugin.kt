package com.skuralll

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

class MCDeployPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.allprojects{
            project ->
            project.tasks.register("exTask") { task ->
                task.doLast {
                    println("Task ${task.name} executed on ${project.name}")
                }
            }
        }
    }
}