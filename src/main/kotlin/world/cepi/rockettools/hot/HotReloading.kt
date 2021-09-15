package world.cepi.rockettools.hot

import net.minestom.server.adventure.audience.Audiences
import org.slf4j.LoggerFactory
import world.cepi.kstom.Manager
import world.cepi.rockettools.Rocket
import world.cepi.rockettools.messaging.MessageHandler
import world.cepi.rockettools.messaging.Translations
import java.nio.file.*
import kotlin.concurrent.thread
import kotlin.io.path.name

object HotReloading {

    val logger = LoggerFactory.getLogger(HotReloading::class.java)

    var running = true

    fun boot() = thread(isDaemon = true, name = "WatchFileSystem") {
        val watchService = FileSystems.getDefault().newWatchService()

        Paths.get("./extensions").register(
            watchService,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_CREATE
        )

        var key: WatchKey
        while (running) {

            key = watchService.take() ?: continue

            forLoop@ for (event in key.pollEvents()) {
                // File must end with .jar
                if (!(event.context() as Path).name.endsWith(".jar")) {
                    key.reset()
                    continue@forLoop
                }

                val foundExtension = Manager.extension.extensions.firstOrNull {
                    it.origin.originalJar?.name == (event.context() as? Path)?.name
                }

                when (event.kind()) {
                    StandardWatchEventKinds.ENTRY_MODIFY -> {

                        if (foundExtension == null) {
                            key.reset()
                            continue@forLoop
                        }

                        Rocket.reload(foundExtension)

                        MessageHandler.sendInfoMessage(
                            Audiences.players(),
                            Translations.reloadHotFinish,
                            foundExtension.origin.name
                        )

                        logger.info("Extension ${foundExtension.origin.name} reloaded.")
                    }
                    StandardWatchEventKinds.ENTRY_CREATE -> {

                        val file = (event.context() as Path).toFile()

                        Rocket.load(file)

                        MessageHandler.sendInfoMessage(Audiences.players(), Translations.hotLoad, file)

                        logger.info("New extension loaded.")
                    }
                }
            }

            key.reset()
        }
    }


    fun stop() {
        running = false
    }

}