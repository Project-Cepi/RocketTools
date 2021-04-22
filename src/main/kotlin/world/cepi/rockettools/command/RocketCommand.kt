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
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.asSubcommand
import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.io.FileOutputStream

internal object RocketCommand : Command("rocket") {

    private fun generateAmountPrefix(inside: NamedTextColor, outside: NamedTextColor, amount: Int): Component {
        return Component.text("(", outside)
            .append(Component.text(amount, inside))
            .append(Component.text(")", outside))
    }
    init {

        val download = "download".asSubcommand()

        val url = ArgumentType.String("urlLink").map { url ->
            try {
                URL(url).toURI()
                url
            } catch (e: Exception) {
                throw ArgumentSyntaxException("URL is invalid", url, 1)
            }
        }
        val jarName = ArgumentType.String("jarName")

        val reload = "reload".asSubcommand()
        val unload = "unload".asSubcommand()
        val list = "list".asSubcommand()
        val info = "info".asSubcommand()

        val extensionArgument = ArgumentType.String("extension").map { extensionName ->
            MinecraftServer.getExtensionManager().extensions.firstOrNull { it.origin.name == name }
                ?: throw ArgumentSyntaxException("Extension not found", extensionName, 1)
        }


        addSyntax(reload, extensionArgument) { sender, args ->
            val extension = args.get(extensionArgument)

            sender.sendMessage(Component.text("Reloading extension ${extension.origin.name}..."))

            MinecraftServer.getExtensionManager().reload(extension.origin.name)

            sender.sendMessage(Component.text("Extension ${extension.origin.name} reloaded!"))
        }

        addSyntax(unload, extensionArgument) { sender, args ->

            val extension = args.get(extensionArgument)

            sender.sendMessage(Component.text("Unloading extension ${extension.origin.name}..."))

            MinecraftServer.getExtensionManager().unloadExtension(extension.origin.name)

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
            val readableByteChannel = Channels.newChannel(URL(args.get(url)).openStream())

            val fileOutputStream = FileOutputStream(File("extensions/" + args.get(jarName) + ".jar"))

            fileOutputStream.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            fileOutputStream.close()
            readableByteChannel.close()
        }

    }

    override fun onDynamicWrite(sender: CommandSender, text: String): Array<out String?> {

        val extensionNames = MinecraftServer.getExtensionManager().extensions.map { it.origin.name }

        return extensionNames.toTypedArray()

    }

}