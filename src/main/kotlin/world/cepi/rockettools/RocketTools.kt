package world.cepi.rockettools

import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.extensions.Extension;
import world.cepi.kstom.Manager
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.rockettools.command.CommandViewer
import world.cepi.rockettools.command.RocketCommand
import world.cepi.rockettools.hot.HotReloading
import world.cepi.rockettools.messaging.MessageHandler
import world.cepi.rockettools.messaging.Translations
import java.nio.file.*
import kotlin.concurrent.thread

class RocketTools : Extension() {

    override fun initialize() {
        CommandViewer.register()
        RocketCommand.register()

        HotReloading.boot()

        logger.info("[RocketTools] has been gaming!")
    }

    override fun terminate() {
        CommandViewer.unregister()
        RocketCommand.unregister()

        HotReloading.stop()

        logger.info("[RocketTools] has been disabled!")
    }

}