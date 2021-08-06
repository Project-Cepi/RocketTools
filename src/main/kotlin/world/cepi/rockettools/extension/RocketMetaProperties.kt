package world.cepi.rockettools.extension

import net.minestom.server.extensions.Extension
import java.net.MalformedURLException
import java.net.URL

/**
 * Convince object for magic strings.
 */
object RocketMetaProperties {

    fun downloadURLFrom(extension: Extension): RocketPropertyValue<URL> {

        if (!extension.origin.meta.has(downloadURL)) {
            return RocketPropertyValue(null, RocketPropertyStage.NONE)
        }

        val downloadURL = extension.origin.meta.getAsJsonPrimitive(downloadURL).asString

        return try {
            RocketPropertyValue(URL(downloadURL))
        } catch (exception: MalformedURLException) {
            RocketPropertyValue(null, RocketPropertyStage.BROKEN)
        }
    }

    const val downloadURL = "downloadURL"

}