package world.cepi.rockettools.command

import com.extollit.misc.RGB.GREEN
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

class RocketCommand : Command("rocket") {

    /*
    Subcommands:
    rocket load <filename> -- Loads a not-loaded extension
    rocket unload <extension name> -- Unloads an extension
    rocket reload <extension name> -- Reloads an extension
     */

    init {
        val arg1 = ArgumentType.Word("arg1")
        val arg2 = ArgumentType.Word("arg2")
        addSyntax({sender, args ->
            val player = sender as Player
            val extension = MinecraftServer.getExtensionManager().getExtension(args.getWord("arg2"))
            if (extension != null) {
                if (args.getWord("arg1") == "reload")
                //MinecraftServer.getExtensionManager().reload(args.getWord("arg2"))
                player.sendMessage("${ChatColor.BRIGHT_GREEN}Extension reloaded!")
            }
        }, arg1, arg2)
    }

}