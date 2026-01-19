package com.duckyduck246.chestforensics.mixin;

import com.duckyduck246.chestforensics.PuedoItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import com.duckyduck246.chestforensics.ChestForensicsClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ContainerDetectionMixin{

    @Inject(method = "updateSlotStacks", at = @At("TAIL"))
    private void chestforensics$onUpdateAll(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        processBulkUpdate(stacks);
    }

    @Inject(method = "setStackInSlot", at = @At("TAIL"))
    private void chestforensics$onSetStack(int slotIndex, int revision, ItemStack stack, CallbackInfo ci) {
        processSingleUpdate(slotIndex, stack);
    }

    private void processBulkUpdate(List<ItemStack> stacks){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<?> handledScreen){
            if (handledScreen.getScreenHandler() instanceof GenericContainerScreenHandler container) {
                int containerSize = container.getRows() * 9;
                ChestForensicsClient.LOGGER.info("batch updates received for: " + containerSize + " slots");
                for (int i = 0; i < Math.min(stacks.size(), containerSize); i++) {
                    ItemStack stack = stacks.get(i);
                    if (!stack.isEmpty()) {
                        ChestForensicsClient.LOGGER.info("Slot " + i + ": " + stack.getName().getString() + " x" + stack.getCount());
                    }
                }
                ArrayList<PuedoItem> compare1 = ChestForensicsClient.getCompare();
                for (int o = 0; o < compare1.size(); o++) {
                    ChestForensicsClient.LOGGER.info("Compared: " + compare1.get(o).getString());
                }
            }
        }
    }

    private void processSingleUpdate(int slotIndex, ItemStack stack){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            if (handledScreen.getScreenHandler() instanceof GenericContainerScreenHandler container) {
                int containerSize = container.getRows() * 9;
                if (slotIndex < containerSize) {
                    ChestForensicsClient.LOGGER.info("single slot " + slotIndex + " update: " + stack.getName().getString());
                }
            }
        }
    }
}