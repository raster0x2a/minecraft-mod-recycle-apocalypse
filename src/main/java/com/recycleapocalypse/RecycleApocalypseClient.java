package com.recycleapocalypse;

import com.recycleapocalypse.client.GachaTableScreen;
import com.recycleapocalypse.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public final class RecycleApocalypseClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.GACHA_TABLE, GachaTableScreen::new);
	}
}
