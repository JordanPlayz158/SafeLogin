package dev.jordanadams.safelogin.mixin;

import dev.jordanadams.safelogin.SafeLogin;
import dev.jordanadams.safelogin.protection.type.MoveProtectionEvaluator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class OnPlayerPushedMixin extends Entity {

  public OnPlayerPushedMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(at = @At("HEAD"), method = "pushAwayFrom")
  public void onPushAwayFromPlayer(Entity entity, CallbackInfo ci) {
    if (entity == this) {
      return;
    }

    if (SafeLogin.INSTANCE.getEvaluator() instanceof MoveProtectionEvaluator moveProtectionEvaluator
        && entity.getType() == EntityType.PLAYER) {
      moveProtectionEvaluator.addPlayerPush(entity.getUuid());

      //SafeLogin.INSTANCE.getLogger().debug("Player '{}' collided with Entity '{}'", entity.getName().getString(), getType().getName().getString());
    }
  }
}
