package dev.jordanadams.safelogin.mixin;

import dev.jordanadams.safelogin.SafeLogin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldBlockBreakMixin {
  @Inject(at = @At("HEAD"), method = "canPlayerModifyAt", cancellable = true)
  public void canBreakBlock(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    if (!SafeLogin.INSTANCE.isPlayerInvulnerable(player.getUuid())) {
      return;
    }

    cir.setReturnValue(false);
  }
}
