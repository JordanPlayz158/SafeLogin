package dev.jordanadams.safelogin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import dev.jordanadams.safelogin.SafeLogin
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class ReloadCommand : Command<ServerCommandSource> {
  override fun run(context: CommandContext<ServerCommandSource>): Int {
    SafeLogin.reload()
    context.source.sendFeedback({ Text.translatable("message.safelogin.reload") }, true)
    return 1;
  }
}