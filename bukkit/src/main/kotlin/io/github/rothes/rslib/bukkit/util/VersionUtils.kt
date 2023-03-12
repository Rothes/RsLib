package io.github.rothes.rslib.bukkit.util

import org.bukkit.Bukkit

object VersionUtils {

    val serverMajorVersion = Bukkit.getServer().bukkitVersion.split('-')[0].split('.')[1].toByte()
    val serverMinorVersion = Bukkit.getServer().bukkitVersion.split('-')[0].split('.').let { if (it.size > 2) it[2].toByte() else 0 }

}