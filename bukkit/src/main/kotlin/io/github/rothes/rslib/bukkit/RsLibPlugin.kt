package io.github.rothes.rslib.bukkit

import com.sk89q.rslib.RsLibDisguisePlugin
import io.github.rothes.rslib.bukkit.config.ConfigManager
import io.github.rothes.rslib.bukkit.i18n.I18n
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.plugin.PluginLogger
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

abstract class RsLibPlugin : JavaPlugin() {

    abstract val configManager: ConfigManager
    abstract val i18n: I18n
    lateinit var adventure: BukkitAudiences

    protected fun hackPrefix(prefix: String) {
        try {
            val logger = JavaPlugin::class.java.getDeclaredField("logger")
            logger.isAccessible = true
            logger[this] = RsLibDisguisePlugin(this)

            val name = PluginLogger::class.java.getDeclaredField("pluginName")
            name.isAccessible = true
            name[this.logger] = prefix
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)
    }

    override fun onDisable() {
        adventure.close()
    }

    fun info(msg: String) {
        getLogger().log(Level.INFO, msg)
    }

    fun warn(msg: String) {
        getLogger().log(Level.WARNING, msg)
    }

    fun error(msg: String) {
        getLogger().log(Level.SEVERE, msg)
    }

    fun info(msg: String, throwable: Throwable) {
        getLogger().log(Level.INFO, msg, throwable)
    }

    fun warn(msg: String, throwable: Throwable) {
        getLogger().log(Level.WARNING, msg, throwable)
    }

    fun error(msg: String, throwable: Throwable) {
        getLogger().log(Level.SEVERE, msg, throwable)
    }

    private val consoleMsgSerializer = LegacyComponentSerializer.builder().build()

    fun info(msg: Component) {
        getLogger().log(Level.INFO, consoleMsgSerializer.serialize(msg))
    }

    fun warn(msg: Component) {
        getLogger().log(Level.WARNING, consoleMsgSerializer.serialize(msg))
    }

    fun error(msg: Component) {
        getLogger().log(Level.SEVERE, consoleMsgSerializer.serialize(msg))
    }

    fun info(msg: Component, throwable: Throwable) {
        getLogger().log(Level.INFO, consoleMsgSerializer.serialize(msg), throwable)
    }

    fun warn(msg: Component, throwable: Throwable) {
        getLogger().log(Level.WARNING, consoleMsgSerializer.serialize(msg), throwable)
    }

    fun error(msg: Component, throwable: Throwable) {
        getLogger().log(Level.SEVERE, consoleMsgSerializer.serialize(msg), throwable)
    }

}