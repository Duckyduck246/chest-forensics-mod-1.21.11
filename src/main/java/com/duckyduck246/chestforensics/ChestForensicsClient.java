package com.duckyduck246.chestforensics;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ChestForensicsClient implements ClientModInitializer {
    public static final String MOD_ID = "chat-logger";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    String containerName = "not yet";
    int containerID = 0;
    public static BlockPos detectedPos = null;

    @Override
    public void onInitializeClient(){
        LOGGER.info("Client Initialized");

        ScreenEvents.AFTER_INIT.register((minecraftClient, screen, i, i1) -> {
           ScreenEvents.remove(screen).register(closedScreen -> {
               if (screen instanceof HandledScreen<?> handledScreen){
                   MinecraftClient client = MinecraftClient.getInstance();
                   containerName = screen.getTitle().getString();
                   containerID = handledScreen.getScreenHandler().syncId;
                   LOGGER.info("Name: " + containerName);
                   LOGGER.info("ID: " + containerID);
                   if (Objects.equals(containerName, "Large Chest")) {

                       LOGGER.info("Pos" + getMainContainer(client.world.getBlockEntity(detectedPos)));
                   }
                   else{
                       LOGGER.info("Pos" + detectedPos);
                   }
                   detectedPos = null;
                   if(client.player != null && client.player.currentScreenHandler != null) {
                       ScreenHandler handler = handledScreen.getScreenHandler();
                       for (int a = 0; a < handler.slots.size(); a++){
                           ItemStack stack = handler.getSlot(a).getStack();
                           if (!stack.isEmpty() && !(handler.getSlot(a).inventory instanceof net.minecraft.entity.player.PlayerInventory)){
                               String nameOfItem = stack.getItem().getName().getString();
                               int count = stack.getCount();
                               LOGGER.info(a + ": " + count + "x " + nameOfItem);
                           }
                       }
                   }
               }
           });
        });;



        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
            String message1 = text.getString();
            String sendername = gameProfile.name();
            LOGGER.info("CHAT: " + message1);
            MinecraftClient client = MinecraftClient.getInstance();
            client.player.sendMessage(text, true);


        ;});
    }

    public static void capturePos(){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget instanceof BlockHitResult blockHit){
            detectedPos = blockHit.getBlockPos();
        }
    }

    public BlockPos getMainContainer(BlockEntity blockEntity){
        if (!(blockEntity instanceof ChestBlockEntity chest)){
            return blockEntity.getPos();
        }
        ChestType type = chest.getCachedState().get(ChestBlock.CHEST_TYPE);
        if (type == ChestType.SINGLE || type == ChestType.LEFT){
            return chest.getPos();
        }
        if (type == ChestType.RIGHT){
            World world = chest.getWorld();
            if (world != null){
                Direction chestFacing = chest.getCachedState().get(ChestBlock.FACING);
                BlockPos neighbor;
                switch(chestFacing){
                    case NORTH: 
                        neighbor = chest.getPos().east();
                        break;
                    case SOUTH:
                        neighbor = chest.getPos().west();
                        break;
                    case WEST:
                        neighbor = chest.getPos().north();
                        break;
                    case EAST:
                        neighbor = chest.getPos().south();
                        break;
                    default:
                        neighbor = chest.getPos().south();
                        break;
                }
                BlockEntity neighborEntity = world.getBlockEntity(neighbor);
                if (neighborEntity instanceof ChestBlockEntity neighborChest){
                    ChestType neighborType = neighborChest.getCachedState().get(ChestBlock.CHEST_TYPE);
                    if (neighborType == ChestType.LEFT){
                        return neighborChest.getPos();
                    }
                }
            }
        }

        return blockEntity.getPos();
    }


}