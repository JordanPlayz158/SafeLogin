package dev.jordanadams.safelogin.mixin;

import dev.jordanadams.safelogin.SafeLogin;
import dev.jordanadams.safelogin.protection.type.MoveProtectionEvaluator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class OnPlayerPushedMixin extends Entity {

  public OnPlayerPushedMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(at = @At("HEAD"), method = "pushAwayFrom", cancellable = true)
  public void onPushAwayFromPlayer(Entity entity, CallbackInfo ci) {
    if (teleportIfPushed(this, entity)) {
      ci.cancel();
    }
  }

  /**
   *
   * @param pusher
   * @param pushed
   * @return true if either entity was teleported
   */
  @Unique
  public boolean teleportIfPushed(Entity pusher, Entity pushed) {
    if (pushed == this) {
      return false;
    }

    if (pusher.getType() != EntityType.PLAYER && pushed.getType() != EntityType.PLAYER) {
      return false;
    }

    if (!(SafeLogin.INSTANCE.getEvaluator() instanceof MoveProtectionEvaluator)) {
      return false;
    }

    boolean wasTeleported = false;
    if (teleportIfInvulnerable(pushed)) {
      SafeLogin.INSTANCE.getLogger().info("{} teleported as {} pushed it away", pushed.getDisplayName().getString(), pusher.getDisplayName().getString());
      wasTeleported = true;
    }

    if (teleportIfInvulnerable(pusher)) {
      SafeLogin.INSTANCE.getLogger().info("{} teleported as {} pushed it away", pusher.getDisplayName().getString(), pushed.getDisplayName().getString());
      wasTeleported = true;
    }

    //SafeLogin.INSTANCE.getLogger().debug("Player '{}' collided with Entity '{}'", entity.getName().getString(), getType().getName().getString());
    return wasTeleported;
  }

  @Unique
  private boolean teleportIfInvulnerable(Entity entity) {
    if (entity.getType() != EntityType.PLAYER) {
      return false;
    }

    BlockPos loginPosition = SafeLogin.INSTANCE.getInvulnerablePlayerPosition(entity.getUuid());

    if (loginPosition == null) {
      return false;
    }

    Vec3d centeredLoginPosition = loginPosition.toCenterPos();
    entity.teleport(centeredLoginPosition.x, centeredLoginPosition.y, centeredLoginPosition.z);
    return true;
  }
}
