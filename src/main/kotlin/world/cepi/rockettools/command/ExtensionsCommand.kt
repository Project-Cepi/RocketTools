package world.cepi.rockettools.command

import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

class ExtensionsCommand : Command("extensions") {

    init {
        setDefaultExecutor {sender: CommandSender, args: Arguments ->
            val player = sender as Player
            var msg = ""
            val list = MinecraftServer.getExtensionManager().extensions
            for (bruh in list) {
                msg += bruh.description.name
                msg += " "
            }
            player.sendMessage("(${ChatColor.DARK_GREEN}${list.size}${ChatColor.WHITE}) ${ChatColor.BRIGHT_GREEN}${msg}")
        }
    }

}