package world.cepi.rockettools

import net.minestom.server.extensions.Extension
import world.cepi.rockettools.command.CommandViewer
import world.cepi.rockettools.command.RocketCommand
import world.cepi.rockettools.hot.HotReloading

class RocketTools : Extension() {

    override fun initialize() {
        CommandViewer.register()
        RocketCommand.register()

        HotReloading.boot()

        logger.info("[RocketTools] has been enabled!")
    }

    override fun terminate() {
        CommandViewer.unregister()
        RocketCommand.unregister()

        HotReloading.stop()

        logger.info("[RocketTools] has been disabled!")
    }

}