package com.anthonyhilyard.highlighter;

import com.anthonyhilyard.iceberg.util.ItemColor;
import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.events.NewItemPickupEvent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

@SuppressWarnings("null")
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Highlighter
{
	public static final ResourceLocation NEW_ITEM_MARKS = new ResourceLocation(Loader.MODID, "textures/gui/newitemmarks.png");

	private static Set<Integer> markedSlots = new HashSet<Integer>(36);

	@SubscribeEvent
	public static void preItemPickup(EntityItemPickupEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = event.getEntity();
		ItemStack item = event.getItem().getItem();
		
		if (player != null && player.equals(mc.player))
		{
			handlePreItemPickup(player, item);
		}
	}

	@SubscribeEvent
	public static void newItemPickup(NewItemPickupEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = event.getEntity();
		ItemStack item = event.getItemStack();

		if (player != null && player.equals(mc.player))
		{
			handlePreItemPickup(player, item);
		}
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
		if (HighlighterConfig.INSTANCE.clearOnInventoryClose.get())
		{
			markedSlots.clear();
		}
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		if (HighlighterConfig.INSTANCE.clearOnHover.get())
		{
			// This event can be raised from any sort of tooltip, but we only care about item tooltips 
			// when the inventory is open, so ensure that is the case.
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen != null && mc.screen instanceof AbstractContainerScreen<?> invScreen)
			{
				Slot slot = invScreen.getSlotUnderMouse();
				if (slot != null && slot.getItem() == event.getItemStack())
				{
					markedSlots.remove(slot.getSlotIndex());
				}
			}
		}
	}

	public static void renderNewItemMark(PoseStack poseStack, Slot slot)
	{
		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slot.getSlotIndex()) && slot.hasItem())
			{
				render(poseStack, slot.getItem(), slot.x, slot.y);
			}
			else
			{
				// If this slot doesn't contain a item, don't display a mark.
				markedSlots.remove(slot.getSlotIndex());
			}
		}
	}

	public static void renderHotBarItemMark(int slotIndex, PoseStack poseStack, ItemStack item, int x, int y)
	{
		if (!HighlighterConfig.INSTANCE.showOnHotbar.get())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slotIndex))
			{
				poseStack.pushPose();
				poseStack.translate(0, 0, -100);
				render(poseStack, item, x, y);
				poseStack.popPose();
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

		if (HighlighterConfig.INSTANCE.useItemNameColor.get())
		{
			// Grab the item's color.  This should match the color of the item's name in the tooltip.
			color = ItemColor.getColorForItem(item, color);
		}

		RenderSystem.disableDepthTest();

		poseStack.pushPose();
		poseStack.translate(0, -Easing.Ease(0, 1, timeOffset), 390);

		RenderSystem.setShaderTexture(0, NEW_ITEM_MARKS);
		RenderSystem.setShaderColor((color.getValue() >> 16 & 255) / 255.0f, (color.getValue() >> 8 & 255) / 255.0f, (color.getValue() & 255) / 255.0f, 1.0f);
		Gui.blit(poseStack, x, y, HighlighterConfig.INSTANCE.useItemNameColor.get() ? 8 : 0, 0, 8, 8, 16, 16);

		poseStack.popPose();
	}
}
