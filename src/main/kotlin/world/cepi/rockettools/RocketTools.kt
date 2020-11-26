package world.cepi.rockettools

import net.minestom.server.extensions.Extension;

class RocketTools : Extension() {

    override fun initialize() {
        logger.info("[RocketTools] has been enabled!")
    }

    override fun terminate() {
        logger.info("[RocketTools] has been disabled!")
    }

}