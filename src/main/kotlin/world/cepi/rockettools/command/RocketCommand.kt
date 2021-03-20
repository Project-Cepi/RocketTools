package world.cepi.rockettools.command

import net.kyori.adventure.text.Component
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
                sender.sendMessage(Component.text("Extension reloaded!"))
            }
        }

        addSyntax(unload, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))
            if (extension != null) {
                MinecraftServer.getExtensionManager().unloadExtension(args.get(extensionName))
                sender.sendMessage(Component.text("Extension unloaded!"))
            }
        }

        addSyntax(list) { sender ->
            val message = MinecraftServer.getExtensionManager().extensions.joinToString { it.description.name }
            sender.sendMessage(
                Component.text("(", NamedTextColor.WHITE)
                    .append(Component.text(MinecraftServer.getExtensionManager().extensions.size, NamedTextColor.DARK_GREEN))
                    .append(Component.text(")", NamedTextColor.WHITE))
                    // End of prefix.
                    .append(Component.space())
                    .append(Component.text(message, NamedTextColor.GREEN))
            )
        }

        addSyntax(info, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.get(extensionName))
            if (extension != null) {
                sender.sendMessage(Component.text("Name: ${extension.description.name}"))
                sender.sendMessage(Component.text("Version: ${extension.description.version}"))
                sender.sendMessage(Component.text("Authors: ${extension.description.authors.joinToString()}"))
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