package io.github.rothes.rslib.bukkit.command

import org.bukkit.command.CommandSender

interface ICommand {

    val name: String
    val children: MutableList<ICommand>
    val execute: (CommandSender, Array<String>) -> Result
    val tab: (CommandSender, Array<String>) -> List<String>
    val descriptionKey: String
    val alias: Array<String>
    val permission: String?
    val parent: ICommand?

    fun childrenNames() = children.map { it.name.lowercase() }

    enum class Result {
        COMPLETED,
        UNKNOWN_COMMAND,
    }

}