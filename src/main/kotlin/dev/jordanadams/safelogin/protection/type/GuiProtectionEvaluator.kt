package dev.jordanadams.safelogin.protection.type

import dev.jordanadams.safelogin.SafeLogin
import dev.jordanadams.safelogin.protection.ProtectionEvaluator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.Generic3x3ContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class GuiProtectionEvaluator : ProtectionEvaluator {
  override fun evaluate(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos): Boolean {
    if (player.blockPos != proposedPosition) {
      player.openHandledScreen(object : NamedScreenHandlerFactory {
        override fun getDisplayName() = Text.translatable("message.safelogin.gui_protection")

        override fun createMenu(
          syncId: Int,
          playerInventory: PlayerInventory?,
          player: PlayerEntity?
        ) : ScreenHandler {
          return Generic3x3ContainerScreenHandler(syncId, playerInventory, object : SimpleInventory(9) {
            override fun onClose(player: PlayerEntity?) {
              super.onClose(player)
              SafeLogin.disableInvulnerability(player as ServerPlayerEntity)
            }
          })
        }
      })
    }

    return false
  }
}