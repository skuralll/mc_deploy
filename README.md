# MC-Deploy Plugin ![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.skuralll.mc_deploy)

## Overview
The MC-Deploy Plugin is a Gradle plugin designed to build and deploy Spigot plugins to a Minecraft server. It transfers the built plugin to the server and reloads it automatically.

## Usage

1. **Add this Gradle plugin to your project.**  
https://plugins.gradle.org/plugin/io.github.skuralll.mc_deploy

2. **Create a `mc_deploy.properties` file in your project directory** with the following content:
   ```properties
   user=Username for connecting to the server
   host=Server address
   ssh_port=Port used for SSH connection
   ssh_key=Path to the SSH private key
   local_path=Path to the plugin file to transfer
   remote_path=Path on the server where the plugin will be placed
   rcon_port=Port used for RCON
   rcon_password=Password for RCON
   plugin_name=Name of the plugin
    ```
3. **Run the mcDeploy task**  
   The plugin will connect to the server, transfer the plugin file, and reload it automatically.
