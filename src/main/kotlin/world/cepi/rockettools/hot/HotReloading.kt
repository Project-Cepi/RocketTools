package world.cepi.rockettools.hot

import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.utils.time.TimeUnit
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

        while (running) {

            val key = watchService.take() ?: continue

            forLoop@ for (event in key.pollEvents()) {
                // File must end with .jar
                if (!(event.context() as Path).name.endsWith(".jar")) {
                    continue@forLoop
                }

                val foundExtension = Manager.extension.extensions.firstOrNull {
                    it.origin.originalJar?.name == (event.context() as? Path)?.name
                }

                when (event.kind()) {
                    StandardWatchEventKinds.ENTRY_MODIFY -> {

                        if (foundExtension == null) {
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
                    StandardWatchEventKinds.ENTRY_CREATE -> Manager.scheduler.buildTask {

                        val fileName = (event.context() as Path).fileName
                        val file = Path.of("./extensions/$fileName").toFile()

                        Manager.extension.extensions.firstOrNull { it.origin.originalJar?.name == file.name }
                            ?.let {
                                Rocket.unload(it)
                                Rocket.load(file)
                                MessageHandler.sendInfoMessage(Audiences.all(), Translations.hotLoad, fileName)
                            }
                    }.delay(5, TimeUnit.SERVER_TICK).schedule()
                }
            }

            running = key.reset()
        }
    }


    fun stop() {
        running = false
    }

}