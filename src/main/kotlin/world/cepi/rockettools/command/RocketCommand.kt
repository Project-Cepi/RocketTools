package world.cepi.rockettools.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import world.cepi.kstom.Manager
import world.cepi.kstom.command.addSubcommands
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.SuggestionIgnoreOption
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.rockettools.Rocket
import world.cepi.rockettools.command.subcommands.RocketUpdateSubcommand
import world.cepi.rockettools.downloadURL
import world.cepi.rockettools.extension.RocketStageCalculator
import world.cepi.rockettools.messaging.MessageHandler
import world.cepi.rockettools.messaging.Translations
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

internal object RocketCommand : Command("rocket") {

    const val downloadURL = "downloadURL"

    private fun generateAmountPrefix(inside: NamedTextColor, outside: NamedTextColor, amount: Int): Component {
        return Component.text("(", outside)
            .append(Component.text(amount, inside))
            .append(Component.text(")", outside))
    }

    val extensionArgument = ArgumentType.String("extension").map { extensionName ->
        MinecraftServer.getExtensionManager().getExtension(extensionName)
            ?: throw ArgumentSyntaxException("Extension $extensionName not found", extensionName, 1)
    }.suggest(SuggestionIgnoreOption.IGNORE_CASE) {
        Manager.extension.extensions.map { it.origin.name }
    }

    init {

        val download = "download".literal()
        val reload = "reload".literal()
        val unload = "unload".literal()
        val list = "list".literal()
        val info = "info".literal()

        extensionArgument.setCallback { sender, exception ->
            sender.sendMessage(Component.text(exception.message!!, NamedTextColor.RED))
        }


        addSyntax(reload, extensionArgument) {
            val extension = context.get(extensionArgument)

            MessageHandler.sendInfoMessage(sender, Translations.reloadProgress, extension.origin.name)

            Rocket.reload(extension)

            MessageHandler.sendInfoMessage(sender, Translations.reloadFinished, extension.origin.name)
        }

        addSyntax(unload, extensionArgument) {

            val extension = context.get(extensionArgument)

            MessageHandler.sendInfoMessage(sender, Translations.unloadProgress, extension.origin.name)

            Rocket.unload(extension)

            MessageHandler.sendInfoMessage(sender, Translations.unloadFinished, extension.origin.name)

        }

        addSyntax(list) {
            sender.sendMessage(
                generateAmountPrefix(NamedTextColor.DARK_GREEN, NamedTextColor.WHITE, MinecraftServer.getExtensionManager().extensions.size)
                    .append(Component.space())
                    .append(MinecraftServer.getExtensionManager().extensions
                        .map {
                            Component.text(it.origin.name, RocketStageCalculator.from(it).color)
                                .hoverEvent(HoverEvent.showText(
                                    Component.text("Info about ${it.origin.name}", NamedTextColor.GRAY)
                                        .append(Component.newline())

                                ))
                                .clickEvent(ClickEvent.runCommand("/rocket info ${it.origin.name}"))
                        }
                        .reduce { acc, textComponent ->
                            acc
                                .append(Component.text(" | ", NamedTextColor.DARK_GREEN))
                                .append(textComponent)
                        })
                    .append(Component.text(" || ", NamedTextColor.GOLD))
                    .append(Path.of("extensions").listDirectoryEntries("*.jar.disabled").map {
                        Component.text(it.name.dropLast(".jar.disabled".length), NamedTextColor.YELLOW)
                    }.fold(Component.empty()) { acc, textComponent ->
                        acc.append(textComponent)
                    })

            )
        }

        addSyntax(info, extensionArgument) {
            val extension = context.get(extensionArgument)
            sender.sendMessage(
                Component.text("- ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Name: ", NamedTextColor.GRAY))
                    .append(Component.text(extension.origin.name, NamedTextColor.WHITE))
                    .append(Component.newline())
                    .append(Component.text("Version: ", NamedTextColor.GRAY))
                    .append(Component.text(extension.origin.version, NamedTextColor.WHITE))
                    .let {
                        if (extension.origin.authors.isNotEmpty()) {
                            return@let it.append(Component.text("Authors: ", NamedTextColor.GRAY))
                                .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.origin.authors.size))
                                .append(Component.space())
                                .append(Component.text(extension.origin.authors.joinToString(), NamedTextColor.WHITE))
                        }

                        else return@let it
                    }
                    .let {
                        if (extension.origin.dependencies.isNotEmpty()) {
                            return@let it.append(Component.newline())
                                .append(Component.text("Dependencies: ", NamedTextColor.GRAY))
                                .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.origin.dependencies.size))
                                .append(Component.space())
                                .let { let ->
                                    let.append(extension.origin.dependencies
                                        .map { dependency ->
                                            Component.text(dependency, NamedTextColor.WHITE)
                                                .hoverEvent(HoverEvent.showText(Component.text("View info about $dependency", NamedTextColor.GRAY)))
                                                .clickEvent(ClickEvent.runCommand("/rocket info $dependency"))
                                        }
                                        .reduce { acc, textComponent ->
                                            acc.append(Component.text(",", NamedTextColor.WHITE))
                                                .append(Component.space())
                                                .append(textComponent)
                                        })
                                }
                        }

                        return@let it
                    }
                    .let {

                        if (extension.dependents.isNotEmpty()) {
                            return@let it.append(Component.newline())
                                .append(Component.text("Dependents: ", NamedTextColor.GRAY))
                                .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.dependents.size))
                                .append(Component.space())
                                .let { let ->
                                    let.append(extension.dependents
                                        .map { dependency ->
                                            Component.text(dependency, NamedTextColor.WHITE)
                                                .hoverEvent(HoverEvent.showText(Component.text("View info about $dependency", NamedTextColor.GRAY)))
                                                .clickEvent(ClickEvent.runCommand("/rocket info $dependency"))
                                        }
                                        .reduce { acc, textComponent ->
                                            acc.append(Component.text(",", NamedTextColor.WHITE))
                                                .append(Component.space())
                                                .append(textComponent)
                                        })
                                }
                        }

                        return@let it
                    }
            )
        }

        addSubcommands(RocketUpdateSubcommand)

    }

}