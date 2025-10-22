package dev.jordanadams.safelogin.protection

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import java.util.UUID

interface ProtectionEvaluator {
  /**
   * @return true when the protection should be disabled
   */
  fun evaluate(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos): Boolean

  /**
   * @return true when the player's moment should be invalidated
   *   (effectively) by teleporting them back to their last position
   */
  fun invalidatePlayerMoment(): Boolean {
    return true
  }

  fun onPlayerDisconnect(uuid: UUID) {}
}