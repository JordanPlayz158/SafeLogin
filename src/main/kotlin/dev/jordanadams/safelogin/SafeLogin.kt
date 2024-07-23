package dev.jordanadams.safelogin

import dev.jordanadams.safelogin.packets.SafeLoginInvulnerabilityS2CPacket
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory
import java.util.UUID

const val MOD_ID = "safelogin"

object SafeLogin : ModInitializer {
	private val logger = LoggerFactory.getLogger(MOD_ID)
	// Player UUIDs invulnerable due to SafeLogin, to be removed on input packet
	private val playersInvulnerable = HashMap<UUID, BlockPos>()

	override fun onInitialize() {
		ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
			toggleInvulnerability(handler.player, true)
		}
	}

	fun ifPlayerMoved(player: ServerPlayerEntity) {
		val loginPosition = playersInvulnerable[player.uuid]

		if (loginPosition !== null && player.blockPos !== loginPosition) {
			toggleInvulnerability(player, false)
		}
	}

	private fun toggleInvulnerability(player: ServerPlayerEntity, invulnerable: Boolean) {
		player.isInvulnerable = invulnerable
		// Ensure hostile mobs won't track to player and huddle around them
		//   and not sure how creepers would react to invulnerable player
		player.isInvisible = invulnerable

		logger.debug(if (invulnerable) {
			"Player '{}' is invulnerable due to login"
		} else {
			"Player '{}' is no longer invulnerable due to moving off of spawn block"
		}, player.gameProfile.name)

		val packet = PacketByteBufs.create()
		packet.writeBoolean(invulnerable)

		// No, could not control it server-side :( otherwise mod would be able to be fully server sided
		ServerPlayNetworking.send(player, SafeLoginInvulnerabilityS2CPacket.ID, packet)

		if (invulnerable) {
			playersInvulnerable[player.uuid] = player.blockPos
		} else {
			playersInvulnerable.remove(player.uuid)
		}
	}
}