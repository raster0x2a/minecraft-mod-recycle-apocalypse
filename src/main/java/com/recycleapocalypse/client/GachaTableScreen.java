package com.recycleapocalypse.client;

import com.recycleapocalypse.menu.GachaTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class GachaTableScreen extends AbstractContainerScreen<GachaTableMenu> {
	private Button rollButton;
	private Button usedButton;
	private Button backButton;
	private Button previousButton;
	private Button nextButton;

	public GachaTableScreen(GachaTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 222);
		this.inventoryLabelY = 124;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

		this.rollButton = addRenderableWidget(Button.builder(Component.translatable("button.recycle_apocalypse.roll"), button -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GachaTableMenu.ROLL_BUTTON_ID);
			}
		}).bounds(this.leftPos + 122, this.topPos + 36, 50, 20).build());

		this.usedButton = addRenderableWidget(Button.builder(Component.translatable("button.recycle_apocalypse.used"), button -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GachaTableMenu.USED_BUTTON_ID);
			}
		}).bounds(this.leftPos + 122, this.topPos + 60, 50, 20).build());

		this.backButton = addRenderableWidget(Button.builder(Component.translatable("button.recycle_apocalypse.back"), button -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GachaTableMenu.BACK_BUTTON_ID);
			}
		}).bounds(this.leftPos + 8, this.topPos + 108, 48, 20).build());

		this.previousButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GachaTableMenu.PREVIOUS_PAGE_BUTTON_ID);
			}
		}).bounds(this.leftPos + 62, this.topPos + 108, 32, 20).build());

		this.nextButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GachaTableMenu.NEXT_PAGE_BUTTON_ID);
			}
		}).bounds(this.leftPos + 138, this.topPos + 108, 32, 20).build());

		updateButtonState();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF1F252B);
		graphics.outline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF6F7D8C);

		if (this.menu.isUsedMode()) {
			drawSlotGrid(graphics, 8, 17, 9, 5);
			graphics.centeredText(this.font, Component.literal((this.menu.usedPage() + 1) + "/" + this.menu.usedPageCount()), this.leftPos + 116, this.topPos + 114, 0xFFE6EDF3);
		} else {
			drawSlotGrid(graphics, 62, 35, 3, 3);
		}

		drawSlotGrid(graphics, 8, 138, 9, 3);
		drawSlotGrid(graphics, 8, 196, 9, 1);
		updateButtonState();
	}

	private void drawSlotGrid(GuiGraphicsExtractor graphics, int x, int y, int columns, int rows) {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				int slotX = this.leftPos + x + column * 18;
				int slotY = this.topPos + y + row * 18;
				graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF101418);
				graphics.outline(slotX - 1, slotY - 1, 18, 18, 0xFF6F7D8C);
			}
		}
	}

	private void updateButtonState() {
		if (this.rollButton == null) {
			return;
		}

		boolean usedMode = this.menu.isUsedMode();
		this.rollButton.visible = !usedMode;
		this.usedButton.visible = !usedMode;
		this.backButton.visible = usedMode;
		this.previousButton.visible = usedMode;
		this.nextButton.visible = usedMode;
		this.previousButton.active = usedMode && this.menu.usedPage() > 0;
		this.nextButton.active = usedMode && this.menu.usedPage() + 1 < this.menu.usedPageCount();
	}
}
