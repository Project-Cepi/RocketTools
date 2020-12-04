package world.cepi.rockettools

import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension;
import world.cepi.rockettools.command.RocketCommand

class RocketTools : Extension() {

    override fun initialize() {

        MinecraftServer.getCommandManager().register(RocketCommand())

        logger.info("[RocketTools] has been enabled!")
    }

    override fun terminate() {
        logger.info("[RocketTools] has been disabled!")
    }

}