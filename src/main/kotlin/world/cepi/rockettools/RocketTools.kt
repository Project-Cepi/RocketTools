package world.cepi.rockettools

import net.minestom.server.extensions.Extension;
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.rockettools.command.CommandViewer
import world.cepi.rockettools.command.RocketCommand

class RocketTools : Extension() {

    override fun initialize() {
        CommandViewer.register()
        RocketCommand.register()

        logger.info("[RocketTools] has been enabled!")
    }

    override fun terminate() {
        CommandViewer.unregister()
        RocketCommand.unregister()

        logger.info("[RocketTools] has been disabled!")
    }

}