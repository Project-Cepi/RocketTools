package world.cepi.rockettools.command

import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import world.cepi.kstom.Manager
import world.cepi.kstom.command.arguments.SuggestionIgnoreOption
import world.cepi.kstom.command.arguments.suggest

object RocketArguments {

    val extensionArgument = ArgumentType.String("extension").map { extensionName ->
        MinecraftServer.getExtensionManager().getExtension(extensionName)
            ?: throw ArgumentSyntaxException("Extension $extensionName not found", extensionName, 1)
    }.suggest(SuggestionIgnoreOption.IGNORE_CASE) {
        Manager.extension.extensions.map { it.origin.name }
    }

}