package io.github.rothes.rslib.bukkit.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

fun Component.replace(search: String, replacement: String): Component {
    return replaceText(TextReplacementConfig.builder().matchLiteral(search).replacement(replacement).build())
}

fun Component.replace(vararg replacements: String): Component {
    if (replacements.size % 2 == 1)
        throw IllegalArgumentException("Replacements size is not even")
    var result = this
    val iterator = replacements.iterator()
    while (iterator.hasNext()) {
        result = result.replace(iterator.next(), iterator.next())
    }
    return result
}

fun Component.replaceRaw(search: String, replacement: String): Component {
    return GsonComponentSerializer.gson().deserialize(GsonComponentSerializer.gson().serialize(this).replace(search, replacement))
}

fun Component.replaceRaw(vararg replacements: String): Component {
    if (replacements.size % 2 == 1)
        throw IllegalArgumentException("Replacements size is not even")
    var raw = GsonComponentSerializer.gson().serialize(this)
    val iterator = replacements.iterator()
    while (iterator.hasNext()) {
        raw = raw.replace(iterator.next(), iterator.next())
    }
    return GsonComponentSerializer.gson().deserialize(raw)
}

fun String.replacep(search: String, replacement: String) = replace(search.placeholder, replacement)

inline val String.placeholder: String
    get() = "<\$$this>"