package world.cepi.rockettools.command.subcommands

import kotlinx.coroutines.*
import net.minestom.server.command.builder.Command
import net.minestom.server.extensions.Extension
import world.cepi.kstom.Manager
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.rockettools.command.RocketCommand
import world.cepi.rockettools.downloadURL
import world.cepi.rockettools.messaging.MessageHandler
import world.cepi.rockettools.messaging.Translations
import java.lang.Exception

internal object RocketUpdateSubcommand : Command("update") {

    /**
     * Updates an extension
     *
     * @param extension The extension to update
     *
     * @return If the extension was updated successfully
     */
    fun update(extension: Extension): Boolean {
        if (extension.origin.originalJar == null) return false
        if (extension.origin.meta.get(RocketCommand.downloadURL)?.asString == null) return false

        try {
            downloadURL(
                extension.origin.meta.get(RocketCommand.downloadURL).asString!!,
                extension.origin.originalJar!!.toPath()
            )
        } catch (exception: Exception) {
            return false
        }

        return true
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    init {

        val all = "all".literal()

        addSyntax(RocketCommand.extensionArgument) {
            val extension = context.get(RocketCommand.extensionArgument)

            if (update(extension)) {
                MessageHandler.sendInfoMessage(sender, Translations.updateSuccess, extension.origin.name)
            } else {
                MessageHandler.sendErrorMessage(sender, Translations.updateFail, extension.origin.name)
            }
        }

        addSyntax(all) {
            ioScope.launch {
                supervisorScope {

                    val deferredList = ArrayList<Deferred<*>>()

                    Manager.extension.extensions.forEach {
                        deferredList.add(async {
                            if (update(it)) {
                                MessageHandler.sendInfoMessage(sender, Translations.updateSuccess, it.origin.name)
                            } else {
                                MessageHandler.sendErrorMessage(sender, Translations.updateFail, it.origin.name)
                            }
                        })
                    }

                    deferredList.joinAll()

                    MessageHandler.sendInfoMessage(sender, Translations.updateAll)
                }
            }
        }
    }

}