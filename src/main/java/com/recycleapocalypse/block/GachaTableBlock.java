package com.recycleapocalypse.block;

import com.recycleapocalypse.menu.GachaTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class GachaTableBlock extends Block {
	private static final Component TITLE = Component.translatable("container.recycle_apocalypse.gacha_table");

	public GachaTableBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.openMenu(new SimpleMenuProvider(
					(id, inventory, menuPlayer) -> new GachaTableMenu(id, inventory, ContainerLevelAccess.create(level, pos)),
					TITLE
			));
		}

		return InteractionResult.SUCCESS;
	}
}
