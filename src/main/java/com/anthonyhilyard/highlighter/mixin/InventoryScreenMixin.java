package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.network.chat.Component;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu>
{
	public InventoryScreenMixin(InventoryMenu p_i51091_1_, Inventory p_i51091_2_, Component p_i51091_3_) { super(p_i51091_1_, p_i51091_2_, p_i51091_3_); }

	@Override
	@Shadow
	protected void renderBg(GuiGraphics graphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) { }

	@Override
	public void onClose()
	{
		super.onClose();
		Highlighter.inventoryClosed();
	}
}
