package dev.jordanadams.safelogin.protection.type

import dev.jordanadams.safelogin.MOD_ID
import dev.jordanadams.safelogin.SafeLogin
import dev.jordanadams.safelogin.protection.ProtectionEvaluator
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import java.util.UUID

class CommandProtectionEvaluator : ProtectionEvaluator {
  val lastSentHelpMessage = HashMap<UUID, Long>()

  override fun evaluate(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos): Boolean {
    val uuid = player.uuid
    val lastHelpMessageTimestamp = lastSentHelpMessage.getOrDefault(uuid, 0)

    val command = SafeLogin.getProtectionDisableCommand()


    if (player.blockPos != proposedPosition && lastHelpMessageTimestamp < System.currentTimeMillis() - 1000) {
      player.sendMessage(Text.translatable("message.safelogin.command_protection", command, MOD_ID))
      lastSentHelpMessage[uuid] = System.currentTimeMillis()
    }

    return false
  }
}
