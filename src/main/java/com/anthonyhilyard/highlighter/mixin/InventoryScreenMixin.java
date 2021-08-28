package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.text.ITextComponent;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin extends DisplayEffectsScreen<PlayerContainer>
{
	public InventoryScreenMixin(PlayerContainer p_i51091_1_, PlayerInventory p_i51091_2_, ITextComponent p_i51091_3_) { super(p_i51091_1_, p_i51091_2_, p_i51091_3_); }

	@Override
	@Shadow
	protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) { }

	@Override
	public void onClose()
	{
		super.onClose();
		Highlighter.inventoryClosed();
	}
}
