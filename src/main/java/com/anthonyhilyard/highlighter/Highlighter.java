package com.anthonyhilyard.highlighter;

import com.anthonyhilyard.iceberg.util.ItemColor;
import com.anthonyhilyard.iceberg.util.Easing;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Highlighter
{
	public static final ResourceLocation NEW_ITEM_MARKS = new ResourceLocation(Loader.MODID, "textures/gui/newitemmarks.png");

	private static Set<Integer> markedSlots = new HashSet<Integer>(36);

	@SubscribeEvent
	public static void preItemPickup(EntityItemPickupEvent event)
	{
		PlayerEntity player = event.getPlayer();
		ItemStack item = event.getItem().getItem();
		
		// First see if there is a stack with available space in the player's inventory.
		int slot = player.inventory.getSlotWithRemainingSpace(item);
		// If not, check for a free slot.
		if (slot == -1)
		{
			slot = player.inventory.getFreeSlot();
		}

		// If we found a valid slot, that's the slot the item should go into.
		// We will mark that as a "new item" slot.
		if (slot != -1)
		{
			markedSlots.add(slot);
		}
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
			if (mc.screen != null && mc.screen instanceof InventoryScreen)
			{
				InventoryScreen invScreen = (InventoryScreen)mc.screen;
				Slot slot = invScreen.getSlotUnderMouse();
				if (slot != null && slot.getItem() == event.getItemStack())
				{
					markedSlots.remove(slot.getSlotIndex());
				}
			}
		}
	}

	public static void renderNewItemMark(MatrixStack matrixStack, Slot slot)
	{
		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slot.getSlotIndex()) && slot.hasItem())
			{
				render(matrixStack, slot.getItem(), slot.x, slot.y);
			}
			else
			{
				// If this slot doesn't contain a item, don't display a mark.
				markedSlots.remove(slot.getSlotIndex());
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void render(MatrixStack matrixStack, ItemStack item, int x, int y)
	{
		if (item.isEmpty())
		{
			return;
		}

		float timeOffset = Math.abs(((Util.getMillis() % 2000) / 1000.0f) - 1.0f);

		// Default to white so the gold-colored icon isn't messed up.
		Color color = Color.fromLegacyFormat(TextFormatting.WHITE);

		if (HighlighterConfig.INSTANCE.useItemNameColor.get())
		{
			// Grab the item's color.  This should match the color of the item's name in the tooltip.
			color = ItemColor.getColorForItem(item, color);
		}

		RenderSystem.disableDepthTest();

		matrixStack.pushPose();
		matrixStack.translate(0, -Easing.Ease(0, 1, timeOffset), 390);

		Minecraft.getInstance().getTextureManager().bind(NEW_ITEM_MARKS);
		RenderSystem.color3f((color.getValue() >> 16 & 255) / 255.0f, (color.getValue() >> 8 & 255) / 255.0f, (color.getValue() & 255) / 255.0f);
		AbstractGui.blit(matrixStack, x, y, HighlighterConfig.INSTANCE.useItemNameColor.get() ? 8 : 0, 0, 8, 8, 16, 16);

		matrixStack.popPose();
	}
}
