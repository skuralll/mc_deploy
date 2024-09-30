package com.skuralll.mc_deploy

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.UserAuthException
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
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
                    /* 設定ファイル読み込み */
                    val propertiesFile = project.file(FILE_NAME)
                    val properties = Properties()
                    if(!propertiesFile.exists()) throw GradleException("Task failed: $FILE_NAME file not found")
                    properties.load(propertiesFile.inputStream())
                    /* ファイル転送 */
                    // SSH
                    checkProperty(properties, "user")
                    checkProperty(properties, "host")
                    checkProperty(properties, "ssh_port")
                    try {
                        SSHClient().use { ssh ->
                            ssh.addHostKeyVerifier(PromiscuousVerifier())
                            ssh.connect(properties.getProperty("host"), properties.getProperty("ssh_port").toInt())
                            // 認証
                            var keyProvider : KeyProvider? = null
                            if (properties.containsKey("ssh_key")){
                                var keyPath = properties.getProperty("ssh_key")
                                if (keyPath.startsWith("~")) keyPath = keyPath.replaceFirst("~", System.getProperty("user.home"))
                                if (!File(keyPath).exists()) throw GradleException("Task failed: $keyPath is not exist.")
                                keyProvider = ssh.loadKeys(keyPath)
                            }
                            if (keyProvider == null){
                                ssh.authPublickey(properties.getProperty("user"))
                            }
                            else{
                                ssh.authPublickey(properties.getProperty("user"), keyProvider)
                            }
                            // SFTP
                            checkProperty(properties, "local_path")
                            checkProperty(properties, "remote_path")
                            val localPath = properties.getProperty("local_path")
                            if (!File(localPath).exists()) throw GradleException("Task failed: $localPath is not exist.")
                            val sftp: SFTPClient = ssh.newSFTPClient()
                            sftp.put(localPath, properties.getProperty("remote_path"))
                            println("Uploaded plugin file.")
                            sftp.close()
                        }
                    } catch (e : UserAuthException){
                        println("Task failed: Authentication failed.")
                        e.printStackTrace()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun checkProperty(properties: Properties, key : String){
        if(!properties.containsKey(key)) throw GradleException("Task failed: The key '${key}' is not set.")
    }

}