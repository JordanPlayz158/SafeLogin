package dev.jordanadams.safelogin.mixin;

import com.mojang.authlib.GameProfile;
import dev.jordanadams.safelogin.SafeLogin;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
public class OnPlayerJoinMixin {
  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;loadPlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/nbt/NbtCompound;"), method = "onPlayerConnect", locals = LocalCapture.CAPTURE_FAILHARD)
  public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player,
      CallbackInfo ci, GameProfile gameProfile, UserCache userCache, String string) {
    SafeLogin.INSTANCE.setCachedPlayerName(player.getUuid(), string);
  }
}
