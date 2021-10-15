package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen
{
	protected AbstractContainerScreenMixin(Component titleIn) { super(titleIn); }

	@Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			shift = Shift.AFTER))
	public void renderSlot(PoseStack poseStack, Slot slot, CallbackInfo info)
	{
		// Only mark items that are in the player's inventory and hotbar.
		if (slot.container instanceof Inventory)
		{
			if (slot.hasItem() && ((Inventory)slot.container).items.contains(slot.getItem()))
			{
				Highlighter.renderNewItemMark(poseStack, slot);
			}
		}
	}
}
