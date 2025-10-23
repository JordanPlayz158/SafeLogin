package dev.jordanadams.safelogin.mixin;

import dev.jordanadams.safelogin.SafeLogin;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class OnPlayerPacketMixin {

  @Shadow public ServerPlayerEntity player;

  @Inject(at = @At("HEAD"), method = "onPlayerMove", cancellable = true)
  private void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
    if (!SafeLogin.INSTANCE.isPlayerInvulnerable(player.getUuid())) {
      return;
    }

    Vec3d position = player.getPos();
    double x = position.getX();
    double y = position.getY();
    double z = position.getZ();

    double packetX = packet.getX(x);
    double packetY = packet.getY(y);
    double packetZ = packet.getZ(z);

    int newX = (int) Math.floor(packetX);
    int newY = (int) Math.floor(packetY);
    int newZ = (int) Math.floor(packetZ);

    BlockPos proposedPosition = new BlockPos(newX, newY, newZ);
    BlockPos acceptedPosition = proposedPosition;
    if (SafeLogin.INSTANCE.getEvaluator().invalidatePlayerMoment()) {
      BlockPos blockPos = player.getBlockPos();
      int blockX = blockPos.getX();
      int blockY = blockPos.getY();
      int blockZ = blockPos.getZ();

      if (blockX != newX || blockY != newY || blockZ != newZ) {
        player.teleport(player.getX(), player.getY(), player.getZ());
        acceptedPosition = player.getBlockPos();
        ci.cancel();
      }
    }

    SafeLogin.INSTANCE.invulnerabilityCheck(player, proposedPosition, acceptedPosition);
  }

  @Inject(at = @At("HEAD"), method = "onPlayerInteractEntity", cancellable = true)
  private void onPlayerAttack(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
    if (!SafeLogin.INSTANCE.isPlayerInvulnerable(player.getUuid())) {
      return;
    }

    ci.cancel();
  }
}
