package world.cepi.rockettools.command.subcommands

import kotlinx.coroutines.*
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
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

    fun updateList(
        extensionList: Collection<Extension>,
        failCallback: (Extension) -> Unit,
        finishCallback: (Int) -> Unit
    ) {
        ioScope.launch {
            supervisorScope {

                val deferredList = ArrayList<Deferred<*>>()

                var failedExtensions = 0

                extensionList.forEach {
                    deferredList.add(async {
                        if (!update(it)) {
                            failCallback(it)
                            failedExtensions++
                        }
                    })
                }

                deferredList.joinAll()

                finishCallback(failedExtensions)
            }
        }
    }

    fun updateToSender(extensionList: Collection<Extension>, sender: CommandSender) {
        updateList(extensionList,
            {
                MessageHandler.sendErrorMessage(sender, Translations.updateFail, it.origin.name)
            },

            finishCallback@ {
                if (it == 0) {
                    MessageHandler.sendInfoMessage(sender, Translations.updateAll)
                    return@finishCallback
                }

                MessageHandler.sendInfoMessage(
                    sender, Translations.updateNumber,
                    Manager.extension.extensions.size - it, Manager.extension.extensions.size
                )
            }
        )
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    init {

        val all = "all".literal()
        val extension = "extension".literal()

        val extensionLoop = ArgumentType.Loop("extensions", RocketCommand.extensionArgument)

        addSyntax(extension, extensionLoop) {
            MessageHandler.sendInfoMessage(sender, Translations.beginUpdating)
            updateToSender(context[extensionLoop], sender)
        }

        addSyntax(all) {
            MessageHandler.sendInfoMessage(sender, Translations.beginUpdating)
            updateToSender(Manager.extension.extensions, sender)
        }
    }

}