package dev.jordanadams.safelogin.protection.type

import dev.jordanadams.safelogin.SafeLogin
import dev.jordanadams.safelogin.protection.ProtectionEvaluator
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

class MoveProtectionEvaluator : ProtectionEvaluator {
  override fun evaluate(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos): Boolean {
    val loginPosition = SafeLogin.getInvulnerablePlayerPosition(player.uuid)!!
    return acceptedPosition != loginPosition
  }

  override fun invalidatePlayerMoment(): Boolean {
    return false
  }
}