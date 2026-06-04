package com.recycleapocalypse.mixin;

import com.recycleapocalypse.gacha.DropFilter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
	private void recycleApocalypse$filterUsedItemDrops(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		ServerLevel level = (ServerLevel) (Object) this;
		if (DropFilter.shouldRemove(entity, level.getServer())) {
			entity.discard();
			cir.setReturnValue(false);
		}
	}
}
