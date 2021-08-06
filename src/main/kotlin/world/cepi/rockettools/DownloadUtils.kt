package world.cepi.rockettools

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.outputStream

/**
 * Downloads a file from a URL and outputs it to a file.
 *
 * @param url The URL to download from
 * @param file The file to put the URL at
 */
fun downloadURL(url: String, file: Path) {
    URL(url).openStream().copyTo(file.outputStream())
}