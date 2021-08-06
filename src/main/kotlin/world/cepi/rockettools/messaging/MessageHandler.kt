package world.cepi.rockettools.messaging

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

object MessageHandler {

    /** Unicode of a meteor of sorts. The opposite of a launching rocket. */
    const val failSymbol = "☄"

    val infoPrefix = Component.text("r ", NamedTextColor.RED)
        .append(Component.text("/ ", TextColor.color(125, 125, 125)))

    private fun sendInfoMessage(audience: Audience, component: Component) =
        audience.sendMessage(infoPrefix.append(component.color(NamedTextColor.GRAY)))

    fun sendInfoMessage(audience: Audience, translationString: String, vararg args: Any?) = sendInfoMessage(
        audience, Component.text(String.format(translationString, *args))
    )

}