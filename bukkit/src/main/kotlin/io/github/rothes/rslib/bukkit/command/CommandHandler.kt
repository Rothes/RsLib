package io.github.rothes.rslib.bukkit.command

import io.github.rothes.rslib.bukkit.RsLibPlugin
import io.github.rothes.rslib.bukkit.command.ICommand.Result.COMPLETED
import io.github.rothes.rslib.bukkit.command.ICommand.Result.UNKNOWN_COMMAND
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.command.Command as BukkitCmd

open class CommandHandler(
    val plugin: RsLibPlugin,
    val name: String,
    val commands : MutableList<ICommand> = mutableListOf(),
) : CommandExecutor, TabCompleter {

    fun register() {
        with (Bukkit.getPluginCommand(name)!!) {
            setExecutor(this@CommandHandler)
            tabCompleter = this@CommandHandler
        }
    }

    override fun onCommand(sender: CommandSender, command: BukkitCmd, label: String, args: Array<String>): Boolean {
        searchCommand(sender, args)?.let {
            when (it.execute(sender, args)) {
                UNKNOWN_COMMAND -> sendHelp(sender, it)
                COMPLETED       -> return true
            }
        } ?: sendHelp(sender, null)

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: BukkitCmd, label: String, args: Array<String>): List<String>? {
        if (args.size == 1) return commands.map { it.name }
        return searchCommand(sender, args)?.let { it.tab(sender, args) }
    }

    private fun searchCommand(sender: CommandSender, args: Array<String>): ICommand? {
        if (args.isEmpty()) return null
        return commands.firstOrNull {
            it.permission?.let(sender::hasPermission) == true
                    && it.name.equals(args[0], true)
        }?.let { root ->
            var command = root
            var index = 1

            while (args.size != index && with(args[index]) {
                    command.children.firstOrNull { child ->
                        child.permission?.let(sender::hasPermission) == true
                                && (child.name.equals(this, true)
                                || child.alias.firstOrNull { it.equals(this, true) } != null)
                    }
                }?.let { command = it } != null) {
                index++
            }
            command
        }
    }

    open fun sendHelp(sender: CommandSender, command: ICommand?) {
        with(plugin.adventure.sender(sender)) {
            sendMessage(plugin.i18n["Sender.Messages.Command.Unknown-Command"])
        }
    }

}