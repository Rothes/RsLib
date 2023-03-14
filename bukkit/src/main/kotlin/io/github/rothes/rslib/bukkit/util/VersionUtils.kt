package io.github.rothes.rslib.bukkit.util

import io.github.rothes.rslib.bukkit.util.version.SemanticVersion
import org.bukkit.Bukkit

object VersionUtils {

    val serverVersion = SemanticVersion(Bukkit.getServer().bukkitVersion.split('-')[0])
    val serverMajorVersion = serverVersion.nums[1].toByte()
    val serverMinorVersion = if (serverVersion.nums.size > 2) serverVersion.nums[2].toByte() else 0

}