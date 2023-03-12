@file:Suppress("DEPRECATION", "removal", "UnstableApiUsage")

package com.sk89q.rslib

import io.github.rothes.rslib.bukkit.RsLibPlugin
import io.papermc.paper.plugin.configuration.PluginMeta
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import java.io.File
import java.io.InputStream
import java.util.logging.Logger

class RsLibDisguisePlugin(private val plugin: RsLibPlugin) : Plugin {


    override fun getDataFolder(): File {
        return plugin.dataFolder
    }

    override fun getDescription(): PluginDescriptionFile {
        return plugin.description
    }
    /**
     * Gets the plugin meta for this plugin.
     * @return configuration
     */
    override fun getPluginMeta(): PluginMeta {
        return plugin.pluginMeta
    }

    override fun getConfig(): FileConfiguration {
        return plugin.config
    }

    override fun getResource(filename: String): InputStream? {
        return plugin.getResource(filename)
    }

    override fun saveConfig() {
        plugin.saveConfig()
    }

    override fun saveDefaultConfig() {
        plugin.saveDefaultConfig()
    }

    override fun saveResource(resourcePath: String, replace: Boolean) {
        plugin.saveResource(resourcePath, replace)
    }

    override fun reloadConfig() {
        plugin.reloadConfig()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("plugin.pluginLoader"))
    @Suppress("removal")
    override fun getPluginLoader(): PluginLoader {
        return plugin.pluginLoader
    }

    override fun getServer(): Server {
        return plugin.server
    }

    override fun isEnabled(): Boolean {
        return plugin.isEnabled
    }

    override fun onLoad() {
        plugin.onLoad()
    }

    override fun onEnable() {
        plugin.onEnable()
    }

    override fun onDisable() {
        plugin.onDisable()
    }

    override fun isNaggable(): Boolean {
        return plugin.isNaggable
    }

    override fun setNaggable(canNag: Boolean) {
        plugin.isNaggable = canNag
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        return plugin.getDefaultWorldGenerator(worldName, id)
    }

    override fun getDefaultBiomeProvider(worldName: String, id: String?): BiomeProvider? {
        return plugin.getDefaultBiomeProvider(worldName, id)
    }

    override fun getLogger(): Logger {
        return plugin.logger
    }

    override fun getName(): String {
        return plugin.name
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return plugin.onCommand(sender, command, label, args)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String>? {
        return plugin.onTabComplete(sender, command, alias, args)
    }

}