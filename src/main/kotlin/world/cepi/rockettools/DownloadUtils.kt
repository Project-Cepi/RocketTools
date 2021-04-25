package world.cepi.rockettools

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

/**
 * Downlaods a file from a URL and outputs it to a file.
 *
 * @param url The URL to download from
 * @param file The file to put the URL at
 */
fun downloadURL(url: String, file: File) {

    val readableByteChannel = Channels.newChannel(URL(url).openStream())

    val fileOutputStream = FileOutputStream(file)

    fileOutputStream.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

    fileOutputStream.close()
    readableByteChannel.close()

}