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
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.rockettools.downloadURL
import java.io.File
import java.net.URL

internal object RocketCommand : Command("rocket") {

    const val downloadURL = "downloadURL"

    private fun generateAmountPrefix(inside: NamedTextColor, outside: NamedTextColor, amount: Int): Component {
        return Component.text("(", outside)
            .append(Component.text(amount, inside))
            .append(Component.text(")", outside))
    }

    init {

        val download = "download".literal()
        val reload = "reload".literal()
        val unload = "unload".literal()
        val list = "list".literal()
        val info = "info".literal()
        val update = "update".literal()

        val jarName = ArgumentType.String("jarName")

        val url = ArgumentType.String("urlLink").map { url ->
            try {
                URL(url).toURI()
                url
            } catch (e: Exception) {
                throw ArgumentSyntaxException("URL is invalid", url, 1)
            }
        }

        url.setCallback { sender, exception ->
            sender.sendMessage(Component.text(exception.message!!, NamedTextColor.RED))
        }

        val extensionArgument = ArgumentType.String("extension").map { extensionName ->
            MinecraftServer.getExtensionManager().getExtension(extensionName)
                ?: throw ArgumentSyntaxException("Extension $extensionName not found", extensionName, 1)
        }.suggest { _, _ ->
            Manager.extension.extensions.map { it.origin.name }.map { SuggestionEntry(it) }.toMutableList()
        }

        extensionArgument.setCallback { sender, exception ->
            sender.sendMessage(Component.text(exception.message!!, NamedTextColor.RED))
        }


        addSyntax(reload, extensionArgument) { sender, args ->
            val extension = args.get(extensionArgument)

            sender.sendMessage(Component.text("Reloading extension ${extension.origin.name}..."))

            Manager.extension.reload(extension.origin.name)

            sender.sendMessage(Component.text("Extension ${extension.origin.name} reloaded!"))
        }

        addSyntax(unload, extensionArgument) { sender, args ->

            val extension = args.get(extensionArgument)

            sender.sendMessage(Component.text("Unloading extension ${extension.origin.name}..."))

            Manager.extension.unloadExtension(extension.origin.name)

            sender.sendMessage(Component.text("Extension ${extension.origin.name} unloaded!"))

        }

        addSyntax(list) { sender ->
            sender.sendMessage(
                generateAmountPrefix(NamedTextColor.DARK_GREEN, NamedTextColor.WHITE, MinecraftServer.getExtensionManager().extensions.size)
                    .append(Component.space())
                    .let { component ->
                        component.append(MinecraftServer.getExtensionManager().extensions
                            .map {
                                Component.text(it.origin.name, NamedTextColor.GREEN)
                                    .hoverEvent(HoverEvent.showText(Component.text("View info about ${it.origin.name}", NamedTextColor.GRAY)))
                                    .clickEvent(ClickEvent.runCommand("/rocket info ${it.origin.name}"))
                            }
                            .reduce { acc, textComponent ->
                                acc.append(Component.text(",", NamedTextColor.GREEN))
                                    .append(Component.space())
                                    .append(textComponent)
                            })
                    }
            )
        }

        addSyntax(info, extensionArgument) { sender, args ->
            val extension = args.get(extensionArgument)
            sender.sendMessage(
                Component.text("- ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Name: ", NamedTextColor.GRAY))
                    .append(Component.text(extension.origin.name, NamedTextColor.WHITE))
            )

            sender.sendMessage(
                Component.text("Version: ", NamedTextColor.GRAY)
                    .append(Component.text(extension.origin.version, NamedTextColor.WHITE))
            )

            if (extension.origin.authors.isNotEmpty())
                sender.sendMessage(
                    Component.text("Authors: ", NamedTextColor.GRAY)
                        .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.origin.authors.size))
                        .append(Component.space())
                        .append(Component.text(extension.origin.authors.joinToString(), NamedTextColor.WHITE))
                )

            if (extension.origin.dependencies.isNotEmpty())
                sender.sendMessage(
                    Component.text("Dependencies: ", NamedTextColor.GRAY)
                        .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.origin.dependencies.size))
                        .append(Component.space())
                        .let {
                            it.append(extension.origin.dependencies
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
                )

            if (extension.dependents.isNotEmpty())
                sender.sendMessage(
                    Component.text("Dependents: ", NamedTextColor.GRAY)
                        .append(generateAmountPrefix(NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, extension.dependents.size))
                        .append(Component.space())
                        .let {
                            it.append(extension.dependents
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
                )
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        addSyntax(download, jarName, url) { _, args ->
            downloadURL(args.get(url), File("extensions/" + args.get(jarName) + ".jar"))
        }

        addSyntax(update, extensionArgument) { _, args ->
            val extension = args.get(extensionArgument)

            if (extension.origin.originalJar == null) return@addSyntax
            if (extension.origin.meta.get(downloadURL)?.asString == null) return@addSyntax

            downloadURL(
                extension.origin.meta.get(downloadURL).asString!!,
                extension.origin.originalJar!!
            )

        }

    }

}