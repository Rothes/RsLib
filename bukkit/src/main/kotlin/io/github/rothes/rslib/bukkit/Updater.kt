package io.github.rothes.rslib.bukkit

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import io.github.rothes.rslib.bukkit.extensions.MessageType
import io.github.rothes.rslib.bukkit.util.EnumUtils
import net.kyori.adventure.text.Component
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.simpleyaml.configuration.file.YamlConfiguration
import org.simpleyaml.utils.SupplierIO
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

open class Updater(
    private val plugin: RsLibPlugin,
    private val metricsId: Int,
    private val gitLink: String,
) : Listener {

    protected open val VERSION_CHANNEL: String
    protected open val VERSION_NUMBER: Int

    private val msgTimesMap = HashMap<String, Int>()
    private val messages: MutableList<Component> = mutableListOf()

    init {
        YamlConfiguration.loadConfiguration(SupplierIO.InputStream { plugin.getResource("plugin.yml") }).run {
            VERSION_CHANNEL = getString("version-channel", "Unknown")
            VERSION_NUMBER = getInt("version-number", -1)
        }
    }

    fun start() {
        initMetrics()
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            try {
                checkJson(getJson())
            } catch (e: IllegalStateException) {
                plugin.error(plugin.i18n.getLocaled("Console-Sender.Messages.Updater.Error-Parsing-Json"), e)
            } catch (e: NullPointerException) {
                plugin.error(plugin.i18n.getLocaled("Console-Sender.Messages.Updater.Error-Parsing-Json"), e)
            } catch (e: JsonSyntaxException) {
                plugin.error(plugin.i18n.getLocaled("Console-Sender.Messages.Updater.Error-Parsing-Json-Syntax"), e)
            } catch (_: IOException) {

            }
        }, 0L, 60 * 60 * 20L)
    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            if (e.player.hasPermission(plugin.name.lowercase(Locale.ROOT) + ".updater.notify")) {
                for (message in messages) {
                    e.player.send(plugin.i18n.getLocaled("Sender.Prefix").append(message))
                }
            }
        })
    }

    private fun Player.send(component: Component) {
        plugin.adventure.player(this).sendMessage(component)
    }

    private fun initMetrics() {
        Metrics(plugin, metricsId)
    }

    private fun getJson(): String {
        URL("https://" + (if (plugin.i18n.systemLocale == "zh-cn") "raw.fastgit.org" else "raw.githubusercontent.com") + gitLink).openStream()
            .use { stream ->
                stream.reader(StandardCharsets.UTF_8).buffered().use { reader ->
                    return StringBuilder().apply {
                        reader.forEachLine { append(it) }
                    }.toString()
//                    var line = reader.readLine()
//                    while (line != null) {
//                        jsonBuilder.append(line).append("\n")
//                        line = reader.readLine()
//                    }
                }
            }
    }

    private fun checkJson(json: String) {
        @Suppress("deprecation") // For legacy server supports
        val element = JsonParser().parse(json)
        val root = element.asJsonObject
        val channels = root.getAsJsonObject("Version_Channels")

        messages.clear()
        if (channels.has(VERSION_CHANNEL)) {
            val channel = channels.getAsJsonObject(VERSION_CHANNEL)
            if (channel.has("Message") && channel.getAsJsonPrimitive("Latest_Version_Number").asInt > VERSION_NUMBER) {
                sendJsonMessage(channel, "updater")
            }
        } else {
            plugin.i18n.getLocaled(
                "Console-Sender.Messages.Updater.Invalid-Channel",
                "<CHANNEL>", VERSION_CHANNEL
            ).run {
                plugin.warn(this)
                messages.add(this)
            }
            return
        }

        for ((key, value) in root.getAsJsonObject("Version_Actions").entrySet()) {
            val split = key.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[1].toInt() >= VERSION_NUMBER && VERSION_NUMBER >= split[0].toInt()) {
                val message = value as JsonObject
                if (message.has("Message")) sendJsonMessage(message, key)
            }
        }
    }

    private fun sendJsonMessage(json: JsonObject, id: String) {
        val msgJson = json.getAsJsonObject("Message")
        val msg = getLocaledJsonMessage(msgJson)
        val msgTimes = if (json.has("Message_Times")) json["Message_Times"].asInt else -1
        var curTimes = msgTimesMap.getOrDefault(id, 0)
        if (msgTimes == -1 || curTimes < msgTimes) {
            if (!json.has("Notify_In_Game") || json["Notify_In_Game"].asBoolean) {
                messages.add(msg)
            }
            when (if (json.has("Log_Level")) json["Log_Level"].asString else "Info") {
                "Error" -> plugin.error(msg)
                "Warn"  -> plugin.warn(msg)
                "Info"  -> plugin.info(msg)
                else    -> plugin.info(msg)
            }
            msgTimesMap[id] = ++curTimes
        }
        checkActions(json.getAsJsonArray("Actions"))
    }

    private fun checkActions(actions: JsonArray?) {
        actions ?: return
        for (action in actions) {
            if (action.asString == "Prohibit") {
                Bukkit.getPluginManager().disablePlugin(plugin)
                return
            }
        }
    }

    private fun getLocaledJsonMessage(messageJson: JsonObject) =
        if (messageJson.has(plugin.i18n.locale())) {
            getTypedMessage(messageJson[plugin.i18n.locale()])
        } else {
            getTypedMessage(messageJson["en-US"])
        }

    private fun getTypedMessage(messageElement: JsonElement) =
        if (messageElement.isJsonObject) {
            getTypedMessage(messageElement.asJsonObject["Message"].asString,
                messageElement.asJsonObject["Type"]?.asString ?: "Legacy")
        } else {
            getTypedMessage(messageElement.asString)
        }

    private fun getTypedMessage(msg: String, type: String = "Legacy") =
        EnumUtils.getEnum(type, MessageType.LEGACY, null).getComponent(msg)
//        when (type) {
//            "MiniMessage"   -> MiniMessage.miniMessage().deserialize(msg)
//            "Json"          -> GsonComponentSerializer.gson().deserialize(msg)
//            "Legacy"        -> LegacyComponentSerializer.legacy('&').deserialize(msg)
//            "Text", "Plain" -> Component.text(msg)
//            else            -> LegacyComponentSerializer.legacy('&').deserialize(msg)
//        }

}