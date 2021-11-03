package com.anthonyhilyard.highlighter;

import com.anthonyhilyard.iceberg.util.ItemColor;
import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.events.NewItemPickupCallback;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class Highlighter implements ClientModInitializer
{
	public static final ResourceLocation NEW_ITEM_MARKS = new ResourceLocation(Loader.MODID, "textures/gui/newitemmarks.png");

	private static Set<Integer> markedSlots = new HashSet<Integer>(36);

	@Override
	public void onInitializeClient()
	{
		HighlighterConfig.init();

		NewItemPickupCallback.EVENT.register(Highlighter::newItemPickup);
		ItemTooltipCallback.EVENT.register(Highlighter::onItemTooltip);
	}

	public static void newItemPickup(UUID uuid, ItemStack itemStack)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.level.getPlayerByUUID(uuid);

		handlePreItemPickup(player, itemStack);
	}

	private static void handlePreItemPickup(Player player, ItemStack item)
	{
		// First see if there is a stack with available space in the player's inventory.
		int slot = player.getInventory().getSlotWithRemainingSpace(item);

		// If not, check for a free slot.
		if (slot == -1)
		{
			slot = player.getInventory().getFreeSlot();
		}

		// If we found a valid slot, that's the slot the item should go into.
		// We will mark that as a "new item" slot.
		if (slot != -1)
		{
			markedSlots.add(slot);
		}
	}

	public static void itemClicked(final int slotIndex)
	{
		markedSlots.remove(slotIndex);
	}

	public static void inventoryClosed()
	{
		if (HighlighterConfig.INSTANCE.clearOnInventoryClose)
		{
			markedSlots.clear();
		}
	}

	public static void onItemTooltip(ItemStack stack, TooltipFlag context, List<Component> lines)
	{
		if (HighlighterConfig.INSTANCE.clearOnHover)
		{
			// This event can be raised from any sort of tooltip, but we only care about item tooltips 
			// when the inventory is open, so ensure that is the case.
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen != null && mc.screen instanceof AbstractContainerScreen)
			{
				AbstractContainerScreen<?> invScreen = (AbstractContainerScreen<?>)mc.screen;
				Slot slot = invScreen.hoveredSlot;
				if (slot != null && slot.getItem() == stack)
				{
					markedSlots.remove(slot.getContainerSlot());
				}
			}
		}
	}

	public static void renderNewItemMark(PoseStack poseStack, Slot slot)
	{
		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slot.getContainerSlot()) && slot.hasItem())
			{
				render(poseStack, slot.getItem(), slot.x, slot.y);
			}
			else
			{
				// If this slot doesn't contain a item, don't display a mark.
				markedSlots.remove(slot.getContainerSlot());
			}
		}
	}

	private static void render(PoseStack poseStack, ItemStack item, int x, int y)
	{
		if (item.isEmpty())
		{
			return;
		}

		float timeOffset = Math.abs(((Util.getMillis() % 2000) / 1000.0f) - 1.0f);

		// Default to white so the gold-colored icon isn't messed up.
		TextColor color = TextColor.fromLegacyFormat(ChatFormatting.WHITE);

		if (HighlighterConfig.INSTANCE.useItemNameColor)
		{
			// Grab the item's color.  This should match the color of the item's name in the tooltip.
			color = ItemColor.getColorForItem(item, color);
		}

		RenderSystem.disableDepthTest();

		poseStack.pushPose();
		poseStack.translate(0, -Easing.Ease(0, 1, timeOffset), 410);

		RenderSystem.setShaderTexture(0, NEW_ITEM_MARKS);
		RenderSystem.setShaderColor((color.getValue() >> 16 & 255) / 255.0f, (color.getValue() >> 8 & 255) / 255.0f, (color.getValue() & 255) / 255.0f, 1.0f);
		Gui.blit(poseStack, x, y, HighlighterConfig.INSTANCE.useItemNameColor ? 8 : 0, 0, 8, 8, 16, 16);

		poseStack.popPose();
	}
}
