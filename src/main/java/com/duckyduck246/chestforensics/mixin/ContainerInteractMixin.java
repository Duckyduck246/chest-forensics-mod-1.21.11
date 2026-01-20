package com.duckyduck246.chestforensics.mixin;

import com.duckyduck246.chestforensics.ChestForensicsClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ContainerInteractMixin {

    @Inject(method = "interactBlock", at = @At("HEAD"))
    private void onInteractBlock(
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        ChestForensicsClient.detectedPos = null;
        ChestForensicsClient.detectedPos = hitResult.getBlockPos();
    }
}
