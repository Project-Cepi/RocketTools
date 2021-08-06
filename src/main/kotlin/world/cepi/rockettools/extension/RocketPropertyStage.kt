package world.cepi.rockettools.extension

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class RocketPropertyStage(val color: TextColor) {

    NONE(NamedTextColor.RED),
    BROKEN(NamedTextColor.YELLOW),
    SET(NamedTextColor.GREEN)

}