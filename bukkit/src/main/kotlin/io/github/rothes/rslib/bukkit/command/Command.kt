package io.github.rothes.rslib.bukkit.command

import org.bukkit.command.CommandSender

class Command(
    override val name: String,
    override val execute: (CommandSender, Array<String>) -> ICommand.Result,
    override val tab: (CommandSender, Array<String>) -> List<String>,
    override val descriptionKey: String,
    override val alias: Array<String> = emptyArray(),
    override val permission: String? = null,
    override val parent: ICommand? = null
) : ICommand {

    override val children: MutableList<ICommand> = mutableListOf()

}