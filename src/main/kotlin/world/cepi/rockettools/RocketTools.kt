package world.cepi.rockettools

import net.minestom.server.extensions.Extension;
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.rockettools.command.RocketCommand

class RocketTools : Extension() {

    override fun initialize() {
        RocketCommand.register()
        logger.info("[RocketTools] has been enabled!")
    }

    override fun terminate() {
        RocketCommand.unregister()
        logger.info("[RocketTools] has been disabled!")
    }

}