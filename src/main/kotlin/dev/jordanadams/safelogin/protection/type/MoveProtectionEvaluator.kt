package dev.jordanadams.safelogin.protection.type

import dev.jordanadams.safelogin.SafeLogin
import dev.jordanadams.safelogin.protection.ProtectionEvaluator
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import java.util.UUID

class MoveProtectionEvaluator : ProtectionEvaluator {
  private val mostRecentPlayerPush = HashMap<UUID, Long>()

  override fun evaluate(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos): Boolean {
    val uuid = player.uuid
    val loginPosition = SafeLogin.getInvulnerablePlayerPosition(uuid)!!

    if (acceptedPosition != loginPosition) {
      val lastPushed = mostRecentPlayerPush.getOrDefault(uuid, 0)
      val oneSecondAgo = System.currentTimeMillis() - 1000
      if (lastPushed < oneSecondAgo) {
        mostRecentPlayerPush.remove(uuid)
        return true
      }

      val centeredLoginPosition = loginPosition.toCenterPos()
      player.teleport(centeredLoginPosition.x, centeredLoginPosition.y, centeredLoginPosition.z)
    }

    return false
  }

  fun addPlayerPush(uuid: UUID) {
    addPlayerPush(uuid, System.currentTimeMillis())
  }

  fun addPlayerPush(uuid: UUID, timestamp: Long) {
    mostRecentPlayerPush[uuid] = timestamp
  }

  override fun invalidatePlayerMoment(): Boolean {
    return false
  }

  override fun onPlayerDisconnect(uuid: UUID) {
    mostRecentPlayerPush.remove(uuid)
  }
}