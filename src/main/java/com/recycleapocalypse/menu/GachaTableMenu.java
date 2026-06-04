package com.recycleapocalypse.menu;

import com.recycleapocalypse.gacha.GachaService;
import com.recycleapocalypse.registry.ModBlocks;
import com.recycleapocalypse.registry.ModMenus;
import com.recycleapocalypse.state.RecycleWorldState;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class GachaTableMenu extends AbstractContainerMenu {
	public static final int INPUT_SIZE = 9;
	private static final int USED_DISPLAY_SIZE = 45;
	public static final int ROLL_BUTTON_ID = 0;
	public static final int USED_BUTTON_ID = 1;
	public static final int BACK_BUTTON_ID = 2;
	public static final int PREVIOUS_PAGE_BUTTON_ID = 3;
	public static final int NEXT_PAGE_BUTTON_ID = 4;
	public static final int MODE_GACHA = 0;
	public static final int MODE_USED = 1;

	private static final int USED_DISPLAY_START = INPUT_SIZE;
	private static final int PLAYER_INVENTORY_START = USED_DISPLAY_START + USED_DISPLAY_SIZE;
	private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
	private static final int HOTBAR_END = PLAYER_INVENTORY_END + 9;

	private final SimpleContainer input = new SimpleContainer(INPUT_SIZE);
	private final SimpleContainer usedDisplay = new SimpleContainer(USED_DISPLAY_SIZE);
	private final ContainerLevelAccess access;
	private final Player owner;
	private final DataSlot mode = DataSlot.standalone();
	private final DataSlot usedPage = DataSlot.standalone();
	private final DataSlot usedPageCount = DataSlot.standalone();

	public GachaTableMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, ContainerLevelAccess.NULL);
	}

	public GachaTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
		super(ModMenus.GACHA_TABLE, containerId);
		this.access = access;
		this.owner = playerInventory.player;

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				addSlot(new OneItemSlot(this.input, column + row * 3, 62 + column * 18, 35 + row * 18, this));
			}
		}

		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 9; column++) {
				addSlot(new UsedDisplaySlot(this.usedDisplay, column + row * 9, 8 + column * 18, 17 + row * 18, this));
			}
		}

		addStandardInventorySlots(playerInventory, 8, 138);
		addDataSlot(this.mode);
		addDataSlot(this.usedPage);
		addDataSlot(this.usedPageCount);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return false;
		}

		if (id == USED_BUTTON_ID) {
			openUsedItems(serverPlayer, 0);
			return true;
		}

		if (id == BACK_BUTTON_ID) {
			this.mode.set(MODE_GACHA);
			this.usedDisplay.clearContent();
			broadcastChanges();
			return true;
		}

		if (id == PREVIOUS_PAGE_BUTTON_ID) {
			openUsedItems(serverPlayer, Math.max(0, this.usedPage.get() - 1));
			return true;
		}

		if (id == NEXT_PAGE_BUTTON_ID) {
			openUsedItems(serverPlayer, Math.min(Math.max(0, this.usedPageCount.get() - 1), this.usedPage.get() + 1));
			return true;
		}

		if (id != ROLL_BUTTON_ID) {
			return false;
		}

		GachaService.GachaResult result = GachaService.rollFromGachaTable(serverPlayer, this.input);
		if (result.success()) {
			serverPlayer.sendSystemMessage(result.message());
		} else {
			serverPlayer.sendSystemMessage(Component.literal("ガチャ失敗: ").append(result.message()));
		}

		broadcastChanges();
		return true;
	}

	private void openUsedItems(ServerPlayer player, int requestedPage) {
		List<String> used = RecycleWorldState.get(player.level().getServer()).usedItemIds();
		int pageCount = Math.max(1, (int) Math.ceil(used.size() / (double) USED_DISPLAY_SIZE));
		int page = Math.max(0, Math.min(requestedPage, pageCount - 1));

		this.usedDisplay.clearContent();
		this.mode.set(MODE_USED);
		this.usedPage.set(page);
		this.usedPageCount.set(pageCount);

		if (used.isEmpty()) {
			broadcastChanges();
			return;
		}

		int start = page * USED_DISPLAY_SIZE;
		int end = Math.min(start + USED_DISPLAY_SIZE, used.size());
		for (int index = start; index < end; index++) {
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(used.get(index)));
			if (item != Items.AIR) {
				this.usedDisplay.setItem(index - start, new ItemStack(item));
			}
		}

		broadcastChanges();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (isUsedMode() || index >= USED_DISPLAY_START && index < PLAYER_INVENTORY_START || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack original = slot.getItem();
		ItemStack copy = original.copy();

		if (index < INPUT_SIZE) {
			if (!moveItemStackTo(original, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveItemStackTo(original, 0, INPUT_SIZE, false)) {
			return ItemStack.EMPTY;
		}

		if (original.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}

		return copy;
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(this.access, player, ModBlocks.GACHA_TABLE);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		clearContainer(player, this.input);
	}

	public boolean isUsedMode() {
		return this.mode.get() == MODE_USED;
	}

	public int usedPage() {
		return this.usedPage.get();
	}

	public int usedPageCount() {
		return this.usedPageCount.get();
	}

	private boolean canPlaceInputItem(ItemStack stack, int targetSlot) {
		if (isUsedMode() || stack.isEmpty()) {
			return false;
		}

		MinecraftServer server = this.owner.level().getServer();
		if (server != null && RecycleWorldState.get(server).isUsed(stack.getItem())) {
			return false;
		}

		for (int slot = 0; slot < INPUT_SIZE; slot++) {
			if (slot == targetSlot) {
				continue;
			}

			ItemStack existing = this.input.getItem(slot);
			if (!existing.isEmpty() && existing.getItem() == stack.getItem()) {
				return false;
			}
		}

		return true;
	}

	private static final class OneItemSlot extends Slot {
		private final GachaTableMenu menu;

		private OneItemSlot(Container container, int slot, int x, int y, GachaTableMenu menu) {
			super(container, slot, x, y);
			this.menu = menu;
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}

		@Override
		public int getMaxStackSize(ItemStack stack) {
			return 1;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return this.menu.canPlaceInputItem(stack, getContainerSlot());
		}

		@Override
		public boolean isActive() {
			return !this.menu.isUsedMode();
		}
	}

	private static final class UsedDisplaySlot extends Slot {
		private final GachaTableMenu menu;

		private UsedDisplaySlot(Container container, int slot, int x, int y, GachaTableMenu menu) {
			super(container, slot, x, y);
			this.menu = menu;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public boolean mayPickup(Player player) {
			return false;
		}

		@Override
		public boolean isActive() {
			return this.menu.isUsedMode();
		}
	}
}
