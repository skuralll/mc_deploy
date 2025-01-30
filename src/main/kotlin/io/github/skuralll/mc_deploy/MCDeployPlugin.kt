package io.github.skuralll.mc_deploy

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.UserAuthException
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import nl.vv32.rcon.Rcon
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.*


class MCDeployPlugin : Plugin<Project> {

    companion object {
        const val FILE_NAME = "mc_deploy.properties"
        const val KEY_USER = "user"
        const val KEY_HOST = "host"
        const val KEY_SSH_PORT = "ssh_port"
        const val KEY_SSH_KEY = "ssh_key"
        const val KEY_LOCAL_PATH = "local_path"
        const val KEY_REMOTE_PATH = "remote_path"
        const val KEY_RCON_PORT = "rcon_port"
        const val KEY_RCON_PASSWORD = "rcon_password"
        const val KEY_PLUGIN_NAME = "plugin_name"
    }

    override fun apply(target: Project) {
        target.allprojects { project ->
            project.tasks.register("mcDeploy") { task ->
                task.doLast {
                    val properties = loadProperties(project)
                    try {
                        sshFileTransfer(properties)
                        reloadPluginViaRcon(properties)
                    } catch (e: GradleException) {
                        println("Task failed: ${e.message}")
                    } catch (e: Exception) {
                        println("An unexpected error occurred:")
                        e.printStackTrace()
                    }
                    println("Finished Task.")
                }
            }
        }
    }

    private fun loadProperties(project: Project): Properties {
        val propertiesFile = project.file(FILE_NAME)
        if (!propertiesFile.exists()) {
            throw GradleException("$FILE_NAME file not found")
        }
        return Properties().apply {
            load(propertiesFile.inputStream())
        }
    }

    private fun sshFileTransfer(properties: Properties) {
        validateProperties(properties, KEY_USER, KEY_HOST, KEY_SSH_PORT, KEY_LOCAL_PATH, KEY_REMOTE_PATH)
        SSHClient().use { ssh ->
            ssh.addHostKeyVerifier(PromiscuousVerifier())
            ssh.connect(properties.getProperty(KEY_HOST), properties.getProperty(KEY_SSH_PORT).toInt())

            authenticate(ssh, properties)
            println("SSH Connected.")

            ssh.newSFTPClient().use { sftp ->
                uploadFile(sftp, properties)
            }
        }
    }

    private fun authenticate(ssh: SSHClient, properties: Properties) {
        val keyProvider: KeyProvider? = properties.getProperty(KEY_SSH_KEY)?.let { keyPath ->
            val resolvedPath = if (keyPath.startsWith("~")) keyPath.replaceFirst("~", System.getProperty("user.home")) else keyPath
            if (!File(resolvedPath).exists()) throw GradleException("SSH key file not found at $resolvedPath")
            ssh.loadKeys(resolvedPath)
        }

        try {
            if (keyProvider != null) {
                ssh.authPublickey(properties.getProperty(KEY_USER), keyProvider)
            } else {
                ssh.authPublickey(properties.getProperty(KEY_USER))
            }
        } catch (e: UserAuthException) {
            throw GradleException("SSH authentication failed")
        }
    }

    private fun uploadFile(sftp: SFTPClient, properties: Properties) {
        val localPath = properties.getProperty(KEY_LOCAL_PATH)
        val remotePath = properties.getProperty(KEY_REMOTE_PATH)
        if (!File(localPath).exists()) {
            throw GradleException("Local file not found: $localPath")
        }
        println("Uploading plugin...")
        sftp.put(localPath, remotePath)
        println("Plugin uploaded to $remotePath.")
    }

    private fun reloadPluginViaRcon(properties: Properties) {
        validateProperties(properties, KEY_HOST, KEY_RCON_PORT, KEY_RCON_PASSWORD, KEY_PLUGIN_NAME)
        executeRconCommands(properties, listOf("plugman reload ${properties.getProperty(KEY_PLUGIN_NAME)}"))
    }

    private fun executeRconCommands(properties: Properties, commands: List<String>) {
        validateProperties(properties, KEY_HOST, KEY_RCON_PORT, KEY_RCON_PASSWORD)
        Rcon.open(properties.getProperty(KEY_HOST), properties.getProperty(KEY_RCON_PORT).toInt()).use { rcon ->
            rcon.authenticate(properties.getProperty(KEY_RCON_PASSWORD))
            commands.forEach { command ->
                val response = rcon.sendCommand(command)
                println("RCON Response for '$command': $response")
            }
        }
        println("RCON disconnected.")
    }

    private fun validateProperties(properties: Properties, vararg keys: String) {
        keys.forEach { key ->
            if (!properties.containsKey(key)) {
                throw GradleException("Missing required property: $key")
            }
        }
    }
}
