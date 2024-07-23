package dev.jordanadams.safelogin

import dev.jordanadams.safelogin.packets.SafeLoginInvulnerabilityS2CPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.UUID

object SafeLoginClient : ClientModInitializer {
	private val logger: Logger = LoggerFactory.getLogger(MOD_ID)
	// Player is not populated when the packet is sent out/on join so using this
	private var safeLoginInvulnerable = false

	override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
			SafeLoginInvulnerabilityS2CPacket.ID) {
				_, _, buf, _ ->
			val invulnerable = buf.readBoolean()

			logger.debug(if (invulnerable) {
				"SafeLoginInvulnerability Packet received, making player un-pushable..."
			} else {
				"SafeLoginInvulnerability Packet received, making player pushable..."
			})
			safeLoginInvulnerable = invulnerable
		}
	}

	fun isPushable(uuid: UUID, cir: CallbackInfoReturnable<Boolean>) {
		if (uuid == MinecraftClient.getInstance().player?.uuid && safeLoginInvulnerable) {
			cir.returnValue = false
		}
	}
}