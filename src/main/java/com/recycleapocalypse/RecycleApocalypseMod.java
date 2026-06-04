package com.recycleapocalypse;

import com.recycleapocalypse.command.RecycleCommand;
import com.recycleapocalypse.registry.ModBlocks;
import com.recycleapocalypse.registry.ModMenus;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RecycleApocalypseMod implements ModInitializer {
	public static final String MOD_ID = "recycle_apocalypse";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModMenus.initialize();
		RecycleCommand.register();
		LOGGER.info("RECYCLE APOCALYPSE initialized");
	}
}
