package io.github.rothes.rslib.bukkit.command

import org.bukkit.command.CommandSender

class Command(
    override val name: String,
    override val execute: (CommandSender, Array<String>) -> ICommand.Result,
    override val tab: (CommandSender, Array<String>) -> List<String> = { _, _ -> emptyList() },
    override val alias: Array<String> = emptyArray(),
    override val permission: String? = null,
    override val parent: ICommand? = null,
    override val descriptionKey: String = "Commands" + StringBuilder().apply {
        var cmd = parent
        while (cmd != null) {
            append('.').append(cmd!!.name)
            cmd = cmd!!.parent
        }
    } + ".Description",
) : ICommand {

    override val children: MutableList<ICommand> = mutableListOf()

}