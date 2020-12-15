package world.cepi.rockettools.command

import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.kstom.addSyntax

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
            val extension = MinecraftServer.getExtensionManager().getExtension(args.getWord("extension"))

            if (extension != null) {
                MinecraftServer.getExtensionManager().reload(extension.description.name)
                sender.sendMessage("Extension reloaded!")
            }
        }

        addSyntax(unload, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.getWord("extension"))
            if (extension != null) {
                MinecraftServer.getExtensionManager().unloadExtension(args.getWord("extension"))
                sender.sendMessage("Extension unloaded!")
            }
        }

        addSyntax(list) { sender ->
            val message = MinecraftServer.getExtensionManager().extensions.joinToString { it.description.name }
            sender.sendMessage("(${ChatColor.DARK_GREEN}${MinecraftServer.getExtensionManager().extensions.size}${ChatColor.WHITE}) ${ChatColor.BRIGHT_GREEN}${message}")
        }

        addSyntax(info, extensionName) { sender, args ->
            val extension = MinecraftServer.getExtensionManager().getExtension(args.getWord("extension"))
            if (extension != null) {
                sender.sendMessage("Name: ${extension.description.name}")
                sender.sendMessage("Version: ${extension.description.version}")
                sender.sendMessage("Authors: ${extension.description.authors.joinToString()}")
            }
        }

        addSyntax(load, loadURL) { ->

        }

        addSyntax(load, loadFile) { ->

        }

    }

    override fun onDynamicWrite(text: String): Array<String> {

        val extensionNames = MinecraftServer.getExtensionManager().extensions.map { it.description.name }

        return extensionNames.toTypedArray()

    }

}