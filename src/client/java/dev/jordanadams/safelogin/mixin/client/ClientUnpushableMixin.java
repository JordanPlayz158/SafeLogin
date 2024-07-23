package dev.jordanadams.safelogin.mixin.client;

import dev.jordanadams.safelogin.SafeLoginClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ClientUnpushableMixin extends Entity {

  @Shadow public abstract boolean isPushable();

  public ClientUnpushableMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(at = @At("HEAD"), method = "isPushable", cancellable = true)
  private void isPushable(CallbackInfoReturnable<Boolean> cir) {
    SafeLoginClient.INSTANCE.isPushable(uuid, cir);
  }
}
