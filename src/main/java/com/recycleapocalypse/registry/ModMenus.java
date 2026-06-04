package com.recycleapocalypse.registry;

import com.recycleapocalypse.RecycleApocalypseMod;
import com.recycleapocalypse.menu.GachaTableMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
	public static final MenuType<GachaTableMenu> GACHA_TABLE = Registry.register(
			BuiltInRegistries.MENU,
			ResourceKey.create(Registries.MENU, Identifier.fromNamespaceAndPath(RecycleApocalypseMod.MOD_ID, "gacha_table")),
			new MenuType<>(GachaTableMenu::new, FeatureFlags.DEFAULT_FLAGS)
	);

	private ModMenus() {
	}

	public static void initialize() {
	}
}
