package io.github.rothes.rslib.bukkit.extensions

import io.github.rothes.rslib.bukkit.config.ComponentType
import io.github.rothes.rslib.bukkit.util.EnumUtils
import io.github.rothes.rslib.bukkit.util.VersionUtils
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.simpleyaml.configuration.ConfigurationSection
import java.lang.reflect.Method
import org.bukkit.Sound as BukkitSound

fun ConfigurationSection.getSound(path: String): Sound? {
    return Sound.sound().apply {
        if (isConfigurationSection(path)) {
            val section = getConfigurationSection(path)
            type(section.getString("Type")?.let {
                EnumUtils.valueOfOrNull<BukkitSound>(it)?.adventureKey ?: return null
            } ?: let {
                Key.key(section.getString("Namespace", "minecraft"), section.getString("Key") ?: return null)
            })
            source(EnumUtils.getEnum(section.getString("Source", "MASTER"), Source.MASTER))
            volume(section.getDouble("Volume", 1.0).toFloat())
            pitch(section.getDouble("Pitch", 1.0).toFloat())

        } else {
            type(EnumUtils.valueOfOrNull<BukkitSound>(getString(path) ?: return null)?.adventureKey ?: return null)
        }
    }.build()
}

fun ConfigurationSection.getTitle(path: String): Title? {
    if (isConfigurationSection(path)) {
        val section = getConfigurationSection(path)
        val title = section.getTypedMessage("Title")
        val subTitle = section.getTypedMessage("SubTitle")
        title ?: subTitle ?: return null

        return Title.title(
            title ?: Component.text(""),
            subTitle ?: Component.text(""),
            Title.Times.times(
                Ticks.duration(section.getLong("Fade-In", 10)),
                Ticks.duration(section.getLong("Stay", 70)),
                Ticks.duration(section.getLong("Fade-Out", 20)),
            ),
        )
    }

    return Title.title(getTypedMessage(path) ?: return null, Component.text(""))
}

fun ConfigurationSection.getTypedMessage(path: String): Component? {
    if (isConfigurationSection(path)) {
        val section = getConfigurationSection(path)
        val type = section.getString("Type", "Legacy")

        return (EnumUtils.valueOfOrNull(type, null) ?: MessageType.values().firstOrNull { enum ->
            enum.aliases.any { it.equals(type, true) }
        })?.run {
            with (section.getString("Message") ?: return null) {
                try {
                    getComponent(this)
                } catch (t: Throwable) {
                    println("Failed to load $type message $this: $t")
                    return null
                }
            }
        } ?: run {
            println("Unknown message type $type.")
            return null
        }
    }

    return MessageType.LEGACY.getComponent(getString(path) ?: return null)
}

fun ConfigurationSection.getTypedMessageType(path: String): ComponentType? {
    if (isConfigurationSection(path)) {
        val section = getConfigurationSection(path)
        val type = section.getString("Type", "Legacy")

        return (EnumUtils.valueOfOrNull(type, null) ?: MessageType.values().firstOrNull { enum ->
            enum.aliases.any { it.equals(type, true) }
        })?.run {
            with (section.getString("Message") ?: return null) {
                try {
                    ComponentType(this@run, this)
                } catch (t: Throwable) {
                    println("Failed to load $type message $this: $t")
                    return null
                }
            }
        } ?: run {
            println("Unknown message type $type.")
            return null
        }
    }

    return ComponentType(MessageType.LEGACY, getString(path) ?: return null)
}

private val getSoundKeyLegacy: Method by lazy {
    with (Class.forName(Bukkit.getServer().javaClass.`package`.name + ".CraftSound").declaredMethods) {
        firstOrNull { it.name == "getSound" }
            ?: first { it.returnType == String::class.java }
    }
}
private val BukkitSound.adventureKey
    get() = if (VersionUtils.serverMajorVersion >= 16) {
        Key.key("minecraft", key.key)
    } else {
        Key.key("minecraft", getSoundKeyLegacy.invoke(null, this) as String)
    }

enum class MessageType(vararg val aliases: String) {
    MINIMESSAGE {
        override fun getComponent(message: String): Component {
            return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message)
        }
    },
    JSON {
        override fun getComponent(message: String): Component {
            return GsonComponentSerializer.gson().deserialize(message)
        }
    },
    LEGACY {
        override fun getComponent(message: String): Component {
            return LegacyComponentSerializer.legacy('&').deserialize(message)
        }
    },
    TEXT("Plain") {
        override fun getComponent(message: String): Component {
            return Component.text(message)
        }
    };

    abstract fun getComponent(message: String): Component
}