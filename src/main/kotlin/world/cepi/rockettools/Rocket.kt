package world.cepi.rockettools

import net.minestom.server.extensions.Extension
import world.cepi.kstom.Manager
import java.io.File

object Rocket {

    const val downloadURL = "downloadURL"

    fun reload(extension: Extension) {
        Manager.extension.reload(extension.origin.name)
    }

    fun unload(extension: Extension) {
        Manager.extension.unloadExtension(extension.origin.name)
    }

    fun load(file: File) {
        Manager.extension.loadDynamicExtension(file)
    }

}