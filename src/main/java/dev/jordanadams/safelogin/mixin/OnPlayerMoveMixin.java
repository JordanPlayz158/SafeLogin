package dev.jordanadams.safelogin.mixin;

import dev.jordanadams.safelogin.SafeLogin;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class OnPlayerMoveMixin {

  @Shadow public ServerPlayerEntity player;

  @Inject(at = @At("HEAD"), method = "onPlayerMove")
  private void onPlayerMove(CallbackInfo ci) {
    SafeLogin.INSTANCE.ifPlayerMoved(player);
  }
}
