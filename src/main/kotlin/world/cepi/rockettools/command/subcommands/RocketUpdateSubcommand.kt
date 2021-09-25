package world.cepi.rockettools.command.subcommands

import kotlinx.coroutines.*
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.extensions.Extension
import world.cepi.kstom.Manager
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.rockettools.Rocket
import world.cepi.rockettools.command.RocketArguments
import world.cepi.rockettools.downloadURL
import world.cepi.rockettools.messaging.MessageHandler
import world.cepi.rockettools.messaging.Translations
import java.lang.Exception

internal object RocketUpdateSubcommand : Kommand({

    val ioScope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Updates an extension
     *
     * @param extension The extension to update
     *
     * @return If the extension was updated successfully
     */
    fun update(extension: Extension): Boolean {
        if (extension.origin.originalJar == null) return false
        if (extension.origin.meta.get(Rocket.downloadURL)?.asString == null) return false

        try {
            downloadURL(
                extension.origin.meta.get(Rocket.downloadURL).asString!!,
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

    val all = "all".literal()
    val extension = "extension".literal()

    val extensionLoop = ArgumentType.Loop("extensions", RocketArguments.extensionArgument)

    syntax(extension, extensionLoop) {
        MessageHandler.sendInfoMessage(sender, Translations.beginUpdating)
        updateToSender(!extensionLoop, sender)
    }

    syntax(all) {
        MessageHandler.sendInfoMessage(sender, Translations.beginUpdating)
        updateToSender(Manager.extension.extensions, sender)
    }


}, "update")