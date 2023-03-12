package io.github.rothes.rslib.bukkit.i18n

import io.github.rothes.rslib.bukkit.RsLibPlugin
import io.github.rothes.rslib.bukkit.exceptions.MissingInitialResourceException
import io.github.rothes.rslib.bukkit.extensions.replace
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.simpleyaml.configuration.file.YamlConfiguration
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

open class I18n(private val plugin: RsLibPlugin) {

    val systemLanguage: String = System.getProperty("user.language", Locale.getDefault().language).lowercase()
    val systemCountry: String = System.getProperty("user.country", Locale.getDefault().country).lowercase()
    val systemLocale: String = "$systemLanguage-$systemCountry"

    private val localeFile: YamlFile = YamlFile()

    private val localedMessages = HashMap<String, Component>()
//    private val replaceHolders = Array(10) { "%$it%" }

    init {
        load()
    }

    fun locale(): String {
        @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS") // May not initialized
        return if (plugin.configManager?.isInitialized() ?: false) plugin.configManager.data.locale ?: systemLocale else systemLocale
    }

    operator fun get(key: String) = getLocaled(key)

    fun getLocaled(key: String, prefixed: Boolean = false): Component = localedMessages[key]?.run {
        return if (prefixed) getLocaled("Sender.Prefix").append(this) else this
    } ?: Component.text("Missing localization key: $key").color(NamedTextColor.DARK_RED)
//
//    fun getLocaled(key: String, vararg replacements: String, prefixed: Boolean = false): Component = getLocaled(key).replaceText(
//        TextReplacementConfig.builder().apply {
//            if (replacements.size % 2 == 1)
//                throw IllegalArgumentException("Replacements size is not even")
//            val iterator = replacements.iterator()
//            while (iterator.hasNext()) {
//                this.matchLiteral(iterator.next())
//                    .replacement(iterator.next())
//            }
//        }.build()
//    ).run { return if (prefixed) getLocaled("Sender.Prefix").append(this) else this }

    fun getLocaled(key: String, vararg replacements: String, prefixed: Boolean = false): Component = getLocaled(key).apply {
            if (replacements.size % 2 == 1)
                throw IllegalArgumentException("Replacements size is not even")
            val iterator = replacements.iterator()
            while (iterator.hasNext()) {
                this.replace(iterator.next(), iterator.next())
            }
    }.run { return if (prefixed) getLocaled("Sender.Prefix").append(this) else this }

    fun getDefaultLocale(): YamlConfiguration = YamlConfiguration().apply {
            load(SupplierIO.Reader { getLocaledAsset("Locales/Locale.yml").reader(StandardCharsets.UTF_8) })
        }

    fun getLocaledAsset(file: String): InputStream {
        return plugin.getResource("Assets/Lang/${locale()}/$file")
            ?: plugin.getResource("Assets/Lang/${systemLocale}/$file")
            ?: plugin.getResource("Assets/Lang/en-us/$file")
            ?: throw MissingInitialResourceException("Assets/Lang/en-us/$file")
    }

    fun load() {
        val file = File(plugin.dataFolder, "/Locale/${locale()}")
        if (file.exists()) {
            checkLocaleKeys(file)
            localeFile.configurationFile = file
            localeFile.load()
        } else {
            file.parentFile.mkdirs()
            file.createNewFile()
            getDefaultLocale().save(file)
            localeFile.configurationFile = file
            localeFile.load()
        }

        for (key in localeFile.getKeys(true)) {
            localedMessages[key] = MiniMessage.miniMessage().deserialize(localeFile.getString(key))
        }
    }

    private fun checkLocaleKeys(localeFile: File) {
        val defaultLocale = getDefaultLocale()
        val locale = YamlFile(localeFile).apply { load() }

        var checked = false
        for (key in defaultLocale.getKeys(true)) {
            if (locale.contains(key))
                continue
            locale.set(key, defaultLocale.get(key))
            checked = true
        }
        if (checked) {
            locale.save()
        }
    }

}