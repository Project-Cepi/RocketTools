package world.cepi.rockettools.extension

import net.minestom.server.extensions.Extension

object RocketStageCalculator {
    fun from(extension: Extension): RocketPropertyStage {

        val values = arrayOf(
            RocketMetaProperties.downloadURLFrom(extension)
        ).map { it.stage }

        if (values.any { it == RocketPropertyStage.NONE }) return RocketPropertyStage.NONE

        if (values.any { it == RocketPropertyStage.BROKEN }) return RocketPropertyStage.BROKEN

        return RocketPropertyStage.SET

    }
}