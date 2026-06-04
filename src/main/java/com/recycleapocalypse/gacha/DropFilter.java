package com.recycleapocalypse.gacha;

import com.recycleapocalypse.state.RecycleWorldState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public final class DropFilter {
	private static final ThreadLocal<Integer> BYPASS_DEPTH = ThreadLocal.withInitial(() -> 0);

	private DropFilter() {
	}

	public static boolean shouldRemove(Entity entity, MinecraftServer server) {
		if (isBypassed() || !(entity instanceof ItemEntity itemEntity)) {
			return false;
		}

		ItemStack stack = itemEntity.getItem();
		return !stack.isEmpty() && RecycleWorldState.get(server).isUsed(stack.getItem());
	}

	public static void withBypass(Runnable runnable) {
		BYPASS_DEPTH.set(BYPASS_DEPTH.get() + 1);
		try {
			runnable.run();
		} finally {
			BYPASS_DEPTH.set(Math.max(0, BYPASS_DEPTH.get() - 1));
		}
	}

	private static boolean isBypassed() {
		return BYPASS_DEPTH.get() > 0;
	}
}
