package io.github.rothes.rslib.bukkit.util

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
                println(msg)
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
                println(it.format(value))
            }
            null
        }
    }

}