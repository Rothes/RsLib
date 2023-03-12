package io.github.rothes.rslib.bukkit.config

import io.github.rothes.rslib.bukkit.RsLibPlugin
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class ConfigManager(private val plugin: RsLibPlugin) {

    val yamlFile: YamlFile = YamlFile(File(plugin.dataFolder.toString() + "/Config.yml"))
    abstract val data: ConfigData

    init {
        val configFile = yamlFile.configurationFile
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            plugin.i18n.getLocaledAsset("Configs/Config.yml").use {
                Files.copy(it, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    abstract fun load()

    open fun isInitialized() = true

    abstract class ConfigData {
        abstract val locale: String?
    }

}