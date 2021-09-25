package world.cepi.rockettools.command.subcommands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.rockettools.downloadURL
import java.net.URL
import java.nio.file.Path

internal object RocketDownloadSubcommand : Kommand({

    val url = "url".literal()
    val github = "github".literal()

    val jarName = ArgumentType.String("jarName")

    val urlLink = ArgumentType.String("urlLink").map { url ->
        try {
            URL(url).toURI()
            url
        } catch (e: Exception) {
            throw ArgumentSyntaxException("URL is invalid", url, 1)
        }
    }

    val githubLink = ArgumentType.String("githubLink")

    urlLink.setCallback { sender, exception ->
        sender.sendMessage(Component.text(exception.message!!, NamedTextColor.RED))
    }

    syntax(url, urlLink, jarName) {
        downloadURL(context.get(urlLink), Path.of("extensions", context.get(jarName) + ".jar"))
    }

    syntax(github, githubLink, jarName) {

    }


}, "download")