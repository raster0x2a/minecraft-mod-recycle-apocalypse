package com.recycleapocalypse.command;

import com.mojang.brigadier.context.CommandContext;
import com.recycleapocalypse.gacha.DropFilter;
import com.recycleapocalypse.gacha.GachaService;
import com.recycleapocalypse.registry.ModBlocks;
import com.recycleapocalypse.state.RecycleWorldState;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RecycleCommand {
	private static final int USED_PAGE_SIZE = 10;

	private RecycleCommand() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				Commands.literal("recycle")
							.then(Commands.literal("give_table").executes(RecycleCommand::executeGiveTable))
							.then(Commands.literal("used")
									.executes(context -> executeUsed(context, 1))
								.then(Commands.argument("page", IntegerArgumentType.integer(1))
										.executes(context -> executeUsed(context, IntegerArgumentType.getInteger(context, "page")))))
		));
	}

	private static int executeGiveTable(CommandContext<CommandSourceStack> context) {
		ServerPlayer player;
		try {
			player = context.getSource().getPlayerOrException();
		} catch (Exception exception) {
			context.getSource().sendFailure(Component.literal("このコマンドはプレイヤーのみ実行できます。"));
			return 0;
		}

		DropFilter.withBypass(() -> GachaService.giveOrDrop(player, new ItemStack(ModBlocks.GACHA_TABLE)));
		context.getSource().sendSuccess(() -> Component.literal("ガチャテーブルを付与しました。"), false);
		return 1;
	}

	private static int executeUsed(CommandContext<CommandSourceStack> context, int page) {
		List<String> used = RecycleWorldState.get(context.getSource().getServer()).usedItemIds();
		if (used.isEmpty()) {
			context.getSource().sendSuccess(() -> Component.literal("消滅済みアイテムはまだありません。"), false);
			return 1;
		}

		int pageCount = Math.max(1, (int) Math.ceil(used.size() / (double) USED_PAGE_SIZE));
		int currentPage = Math.min(page, pageCount);
		int start = (currentPage - 1) * USED_PAGE_SIZE;
		int end = Math.min(start + USED_PAGE_SIZE, used.size());

		context.getSource().sendSuccess(() -> Component.literal("消滅済みアイテム " + currentPage + "/" + pageCount + " (" + used.size() + "種)"), false);
		for (String itemId : used.subList(start, end)) {
			context.getSource().sendSuccess(() -> Component.literal("- " + itemId), false);
		}

		return 1;
	}
}
