package world.cepi.rockettools.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.asSubcommand

object RocketCommand : Command("rocket") {

    fun generateAmountPrefix(inside: NamedTextColor, outside: NamedTextColor, amount: Int): Component {
        return Component.text("(", outside)
            .append(Component.text(amount, inside))
            .append(Component.text(")", outside))
    }

    init {

        val load = "load".asSubcommand()
        val loadURL = "url".asSubcommand()
        val loadFile = ArgumentType.DynamicWord("file")

        val reload = "reload".asSubcommand()
        val unload = "unload".asSubcommand()
        val list = "list".asSubcommand()
        val info = "info".asSubcommand()

        val extensionName = ArgumentType.DynamicWord("extension").fromRestrictions { name ->
           MinecraftServer.getExtensionManager().extensions.any { it.origin.name == name }
        }


        addSyntax(reload, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))

            if (extension != null) {
                sender.sendMessage(Component.text("Reloading extension ${extension.origin.name}..."))

                MinecraftServer.getExtensionManager().reload(extension.origin.name)

                sender.sendMessage(Component.text("Extension ${extension.origin.name} reloaded!"))
            }
        }

        addSyntax(unload, extensionName) { sender, args ->

            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))
            if (extension != null) {

                sender.sendMessage(Component.text("Unloading extension ${extension.origin.name}..."))

                MinecraftServer.getExtensionManager().unloadExtension(args.get(extensionName))

                sender.sendMessage(Component.text("Extension ${extension.origin.name} unloaded!"))

            }
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

        addSyntax(info, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))
            if (extension != null) {
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
        }

        addSyntax(load, loadURL) { ->

        }

        addSyntax(load, loadFile) { ->

        }

    }

    override fun onDynamicWrite(sender: CommandSender, text: String): Array<out String?> {

        val extensionNames = MinecraftServer.getExtensionManager().extensions.map { it.origin.name }

        return extensionNames.toTypedArray()

    }

}