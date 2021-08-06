package world.cepi.rockettools.extension

data class RocketPropertyValue<T>(
    val obj: T?,
    val stage: RocketPropertyStage =
        if (obj == null)
            RocketPropertyStage.NONE
        else
            RocketPropertyStage.SET
)