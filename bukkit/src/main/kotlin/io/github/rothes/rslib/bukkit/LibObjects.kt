package io.github.rothes.rslib.bukkit

import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

object LibObjects {

    var logger: Logger = NamedLogger("RsLib")

    class NamedLogger(name: String): Logger(name, null) {
        init {
            parent = Bukkit.getServer().logger
            level = Level.ALL
        }
    }

}