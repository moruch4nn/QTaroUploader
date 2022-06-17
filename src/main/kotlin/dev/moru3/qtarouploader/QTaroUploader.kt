package dev.moru3.qtarouploader

import dev.moru3.minepie.config.Config
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class QTaroUploader: JavaPlugin() {
    lateinit var config: Config

    private lateinit var jda: JDA
    override fun onEnable() {
        config = Config(this, "config.yml")
        jda = JDABuilder.createDefault(System.getenv("QTAROBOT_TOKEN"))
            .addEventListeners(object: ListenerAdapter() {
                override fun onMessageReceived(event: MessageReceivedEvent) {
                    if(event.channel.type == ChannelType.PRIVATE) {
                        if(config.config()?.getLongList("whitelist")?.contains(event.author.idLong)!=true) { return }
                        val file = event.message.attachments.getOrNull(0)?:return
                        if(file.fileExtension!="jar") { return }
                        file.proxy.downloadToFile(File("./plugin/${file.fileName}"))
                        val message = "${event.author.name}がサーバーにプラグイン${file.fileName}をアップロードしました。"
                        Bukkit.getOnlinePlayers().filter { it.isOp }.forEach { player -> player.sendMessage(message) }
                        server.consoleSender.sendMessage(message)
                    }
                }
            }).build()
    }

    override fun onDisable() {
        jda.shutdownNow()
    }
}