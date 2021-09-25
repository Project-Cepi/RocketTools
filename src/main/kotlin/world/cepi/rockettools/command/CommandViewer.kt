package world.cepi.rockettools.command

import net.kyori.adventure.text.Component
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import world.cepi.kstom.Manager
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.kommand.Kommand

object CommandViewer : Kommand({

    val rootCommand = ArgumentType.Word("command")
        .suggest {
            Manager.command.dispatcher.commands.map {
                it.names.toList()
            }.flatten()
        }.map {
            Manager.command.getCommand(it) ?: throw ArgumentSyntaxException("Invalid command", it, 1)
        }

    syntax(rootCommand) {

        val command = context[rootCommand]

        sender.sendMessage(
            Component.text("Command: ${command.name}")
                .let {
                    if (command.aliases != null) {
                        return@let it
                            .append(Component.newline())
                            .append(Component.text("Aliases: ${command.aliases?.joinToString()}"))
                    }

                    it
                }
        )
    }

}, "command")