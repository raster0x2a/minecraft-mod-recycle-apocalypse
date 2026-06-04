package com.recycleapocalypse.state;

import com.mojang.serialization.Codec;
import com.recycleapocalypse.RecycleApocalypseMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RecycleWorldState extends SavedData {
	private static final Codec<RecycleWorldState> CODEC = Codec.STRING.listOf()
			.xmap(ids -> new RecycleWorldState(new LinkedHashSet<>(ids)), state -> new ArrayList<>(state.usedItemIds));

	private static final SavedDataType<RecycleWorldState> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(RecycleApocalypseMod.MOD_ID, "used_items"),
			RecycleWorldState::new,
			CODEC,
			null
	);

	private final Set<String> usedItemIds;

	public RecycleWorldState() {
		this(new LinkedHashSet<>());
	}

	private RecycleWorldState(Set<String> usedItemIds) {
		this.usedItemIds = usedItemIds;
	}

	public static RecycleWorldState get(MinecraftServer server) {
		ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);
		if (overworld == null) {
			return new RecycleWorldState();
		}

		return overworld.getDataStorage().computeIfAbsent(TYPE);
	}

	public boolean isUsed(Item item) {
		return this.usedItemIds.contains(idOf(item));
	}

	public boolean isUsed(String itemId) {
		return this.usedItemIds.contains(itemId);
	}

	public boolean markUsed(Item item) {
		boolean added = this.usedItemIds.add(idOf(item));
		if (added) {
			setDirty();
		}
		return added;
	}

	public List<String> usedItemIds() {
		return List.copyOf(this.usedItemIds);
	}

	public int usedItemCount() {
		return this.usedItemIds.size();
	}

	public static String idOf(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}
}
