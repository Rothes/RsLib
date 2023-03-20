package io.github.rothes.rslib.bukkit.util

import io.github.rothes.rslib.bukkit.LibObjects

object EnumUtils {

    fun <T : Enum<T>> getEnum(
        value: String,
        fallback: T,
        msg: String? = "Unknown ${fallback.javaClass.simpleName.lowercase()} type $value, using default ${fallback.name}.",
    ): T {
        return try {
            java.lang.Enum.valueOf(fallback.javaClass, value.uppercase())
        } catch (e: IllegalArgumentException) {
            msg?.let {
                LibObjects.logger.severe(msg)
            }
            fallback
        }
    }

    inline fun <reified T : Enum<T>> valueOfOrNull(
        value: String,
        msg: String? = "Unknown ${T::class.java.simpleName.lowercase()} type $value.",
    ): T? {
        return try {
            java.lang.Enum.valueOf(T::class.java, value.uppercase())
        } catch (e: IllegalArgumentException) {
            msg?.let {
                LibObjects.logger.severe(it)
            }
            null
        }
    }

}