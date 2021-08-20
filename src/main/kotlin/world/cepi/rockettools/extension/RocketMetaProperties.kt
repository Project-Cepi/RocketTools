package world.cepi.rockettools.extension

import net.minestom.server.extensions.Extension
import java.net.HttpURLConnection
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

    fun githubFrom(extension: Extension): RocketPropertyValue<String> {
        if (!extension.origin.meta.has(github)) {
            return RocketPropertyValue(null, RocketPropertyStage.NONE)
        }

        val github = extension.origin.meta.getAsJsonPrimitive(github).asString

        return try {
            RocketPropertyValue(if ((URL("https://github.com/$github").openConnection() as HttpURLConnection).also {
                it.connect()
            }.responseCode == 404) github else return RocketPropertyValue(null, RocketPropertyStage.BROKEN))
        } catch (exception: MalformedURLException) {
            RocketPropertyValue(null, RocketPropertyStage.BROKEN)
        }
    }

    const val downloadURL = "downloadURL"
    const val github = "github"

}