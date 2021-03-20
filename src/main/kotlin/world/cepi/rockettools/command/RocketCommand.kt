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

class RocketCommand : Command("rocket") {

    init {

        val load = ArgumentType.Word("load").from("load")
        val loadURL = ArgumentType.Word("url").from("url")
        val loadFile = ArgumentType.DynamicWord("file")

        val reload = ArgumentType.Word("reload").from("reload")
        val unload = ArgumentType.Word("unload").from("unload")
        val list = ArgumentType.Word("list").from("list")
        val info = ArgumentType.Word("info").from("info")

        val extensionName = ArgumentType.DynamicWord("extension").fromRestrictions { name ->
           MinecraftServer.getExtensionManager().extensions.any { it.description.name == name }
        }


        addSyntax(reload, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))

            if (extension != null) {
                MinecraftServer.getExtensionManager().reload(extension.description.name)
                sender.sendMessage(Component.text("Extension ${extension.description.name} reloaded!"))
            }
        }

        addSyntax(unload, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))
            if (extension != null) {
                MinecraftServer.getExtensionManager().unloadExtension(args.get(extensionName))
                sender.sendMessage(Component.text("Extension ${extension.description.name} unloaded!"))
            }
        }

        addSyntax(list) { sender ->
            sender.sendMessage(
                Component.text("(", NamedTextColor.WHITE)
                    .append(Component.text(MinecraftServer.getExtensionManager().extensions.size, NamedTextColor.DARK_GREEN))
                    .append(Component.text(")", NamedTextColor.WHITE))
                    // End of prefix.
                    .append(Component.space())
                    .let {
                        it.append(MinecraftServer.getExtensionManager().extensions
                            .map {
                                Component.text(it.description.name, NamedTextColor.GREEN)
                                    .hoverEvent(HoverEvent.showText(Component.text("View info about ${it.description.name}", NamedTextColor.GRAY)))
                                    .clickEvent(ClickEvent.runCommand("/rocket info ${it.description.name}"))
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
                        .append(Component.text(extension.description.name, NamedTextColor.WHITE))
                )

                sender.sendMessage(
                    Component.text("Version: ", NamedTextColor.GRAY)
                        .append(Component.text(extension.description.version, NamedTextColor.WHITE))
                )

                if (extension.description.authors.size != 0)
                    sender.sendMessage(
                        Component.text("Authors: ", NamedTextColor.GRAY)
                            .append(Component.text(extension.description.authors.joinToString(), NamedTextColor.WHITE))
                    )

                if (extension.description.dependents.size != 0)
                    sender.sendMessage(
                        Component.text("Dependencies: ", NamedTextColor.GRAY)
                            .let {
                                it.append(extension.description.dependents
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

        val extensionNames = MinecraftServer.getExtensionManager().extensions.map { it.description.name }

        return extensionNames.toTypedArray()

    }

}