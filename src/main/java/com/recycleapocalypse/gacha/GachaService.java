package com.recycleapocalypse.gacha;

import com.recycleapocalypse.state.RecycleWorldState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GachaService {
	public static final int COST_COUNT = 9;
	public static final int PRIZE_COUNT = 9;

	private GachaService() {
	}

	public static GachaResult rollFromGachaTable(ServerPlayer player, Container input) {
		RecycleWorldState state = RecycleWorldState.get(player.level().getServer());
		InputValidation validation = validateGachaTableInput(input, state);
		if (!validation.success()) {
			return GachaResult.failure(validation.message());
		}

		Set<Item> costItems = Set.copyOf(validation.costItems());
		List<Item> prizePool = BuiltInRegistries.ITEM.stream()
				.filter(item -> item != Items.AIR)
				.filter(item -> !costItems.contains(item))
				.toList();

		if (prizePool.isEmpty()) {
			return GachaResult.failure(Component.literal("景品候補がありません。"));
		}

		Item prizeItem = prizePool.get(player.getRandom().nextInt(prizePool.size()));
		for (int slot = 0; slot < input.getContainerSize(); slot++) {
			input.removeItem(slot, 1);
		}
		input.setChanged();

		for (Item costItem : validation.costItems()) {
			state.markUsed(costItem);
		}

		ItemStack prizeStack = new ItemStack(prizeItem, PRIZE_COUNT);
		DropFilter.withBypass(() -> giveOrDrop(player, prizeStack));
		playGachaEffects(player);

		return GachaResult.success(
				costItemIds(validation.costItems()),
				RecycleWorldState.idOf(prizeItem),
				Component.literal("ガチャ結果: " + RecycleWorldState.idOf(prizeItem) + " x" + PRIZE_COUNT)
		);
	}

	private static InputValidation validateGachaTableInput(Container input, RecycleWorldState state) {
		if (input.getContainerSize() < COST_COUNT) {
			return InputValidation.failure(Component.literal("入力スロットが不足しています。"));
		}

		Set<Item> seen = new HashSet<>();
		List<Item> costItems = new java.util.ArrayList<>();
		for (int slot = 0; slot < COST_COUNT; slot++) {
			ItemStack stack = input.getItem(slot);
			if (stack.isEmpty() || stack.getCount() != 1) {
				return InputValidation.failure(Component.literal("9スロットすべてにアイテムを1個ずつ配置してください。"));
			}

			Item item = stack.getItem();
			if (state.isUsed(item)) {
				return InputValidation.failure(Component.literal("使用済みアイテムはガチャ素材にできません: " + RecycleWorldState.idOf(item)));
			}

			if (!seen.add(item)) {
				return InputValidation.failure(Component.literal("9種類の別々のアイテムを1個ずつ配置してください。"));
			}

			costItems.add(item);
		}

		return InputValidation.success(costItems);
	}

	private static String costItemIds(List<Item> costItems) {
		return costItems.stream()
				.map(RecycleWorldState::idOf)
				.toList()
				.toString();
	}

	public static void giveOrDrop(ServerPlayer player, ItemStack stack) {
		boolean fullyInserted = player.getInventory().add(stack);
		if (!fullyInserted && !stack.isEmpty()) {
			player.drop(stack, false);
		}
	}

	private static void playGachaEffects(ServerPlayer player) {
		ServerLevel level = player.level();
		double x = player.getX();
		double y = player.getY() + 1.0D;
		double z = player.getZ();

		level.playSound(null, x, y, z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8F, 1.35F);
		level.playSound(null, x, y, z, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.45F, 1.8F);
		level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 36, 0.55D, 0.65D, 0.55D, 0.12D);
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y + 0.2D, z, 12, 0.35D, 0.35D, 0.35D, 0.05D);
	}

	public record GachaResult(boolean success, String costItemId, String prizeItemId, Component message) {
		public static GachaResult success(String costItemId, String prizeItemId, Component message) {
			return new GachaResult(true, costItemId, prizeItemId, message);
		}

		public static GachaResult failure(Component message) {
			return new GachaResult(false, "", "", message);
		}
	}

	private record InputValidation(boolean success, List<Item> costItems, Component message) {
		private static InputValidation success(List<Item> costItems) {
			return new InputValidation(true, List.copyOf(costItems), Component.empty());
		}

		private static InputValidation failure(Component message) {
			return new InputValidation(false, List.of(), message);
		}
	}
}
