package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

@Mixin(ContainerScreen.class)
public class ContainerScreenMixin extends Screen
{
	protected ContainerScreenMixin(ITextComponent titleIn) { super(titleIn); }

	@Inject(method = "renderSlot(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/inventory/container/Slot;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			shift = Shift.AFTER))
	public void renderSlot(MatrixStack matrixStack, Slot slot, CallbackInfo info)
	{
		Highlighter.renderNewItemMark(matrixStack, slot);
	}
}
