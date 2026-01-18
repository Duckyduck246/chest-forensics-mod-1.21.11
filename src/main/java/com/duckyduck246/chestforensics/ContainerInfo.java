package com.duckyduck246.chestforensics;

import net.fabricmc.api.ClientModInitializer;

import com.duckyduck246.chestforensics.ChestForensicsClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class ContainerInfo {
    
    public String type;
    public BlockPos pos;
    public ArrayList<ItemStack> items;
    public ArrayList<String> tags;
    public Direction dir;
    public String id;
    public static int total = 0;
    public boolean doubleChest;
    public BlockPos otherPos;
    
    public ContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d){
        type = t;
        pos = p;
        items = i;
        tags = a;
        dir = d;
        id = "containerId:" + type + pos.toString() + dir.toString();
        doubleChest = false;
        total++;
    }
    
    public ContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d, BlockPos o){
        type = t;
        pos = p;
        items = i;
        tags = a;
        dir = d;
        id = "containerId:" + type + pos.toString() + dir.toString() + otherPos.toString();
        doubleChest = true;
        otherPos = o;
        total++;
    }
    
    public void logInfo(){
        ChestForensicsClient.LOGGER.info("type: " + type);
        ChestForensicsClient.LOGGER.info("tags: " + tags);
        ChestForensicsClient.LOGGER.info("direction: " + dir);
        ChestForensicsClient.LOGGER.info(id);
        ChestForensicsClient.LOGGER.info("isDoubleChest? " + doubleChest);
        ChestForensicsClient.LOGGER.info("pos: " + pos);
        if(doubleChest){
            ChestForensicsClient.LOGGER.info("other pos: " + id);
        }
        ChestForensicsClient.LOGGER.info("items: " + items);   
    }
    
    public void logTotal(){
        ChestForensicsClient.LOGGER.info("" + total);
    }

    public static ArrayList<ItemStack> listItems(int mode){
        MinecraftClient client = MinecraftClient.getInstance();
        switch(mode) {
            case 1:
                if(client.player != null && client.player.currentScreenHandler != null) {
                    if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
                        ScreenHandler handler = handledScreen.getScreenHandler();
                        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                        for (int a = 0; a < handler.slots.size(); a++) {
                            ItemStack stack = handler.getSlot(a).getStack();
                            if (!stack.isEmpty() && !(handler.getSlot(a).inventory instanceof net.minecraft.entity.player.PlayerInventory)) {
                                items.add(stack);
                                String nameOfItem = stack.getItem().getName().getString();
                                int count = stack.getCount();
                                ChestForensicsClient.LOGGER.info(a + ": " + count + "x " + nameOfItem);
                            }
                        }
                        return items;
                    }
                }
                break;
            case 2:
                if(client.player != null && client.player.currentScreenHandler != null) {
                    if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
                        ScreenHandler handler = handledScreen.getScreenHandler();
                        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                        for (int a = 0; a < handler.slots.size(); a++) {
                            ItemStack stack = handler.getSlot(a).getStack();
                            if (!stack.isEmpty() && !(handler.getSlot(a).inventory instanceof net.minecraft.entity.player.PlayerInventory)) {
                                items.add(stack);
                                String nameOfItem = stack.getItem().getName().getString();
                                int count = stack.getCount();
                            }
                        }
                        return items;
                    }
                }
                break;

        }
    return null;
    }

    public ArrayList<ItemStack> compareItems(ArrayList<ItemStack> a, ArrayList<ItemStack> b){
        if(a.size() == b.size()){
            for(int i = 0; i < a.size(); i++){
                ItemStack stackA = a.get(i);
                ItemStack stackB = b.get(i);
                int countA = stackA.getCount();
                int countB = stackB.getCount();
                if(!stackA.equals(stackB)){
                    if(!stackA.getItem().getName().equals(stackB.getItem().getName())) {

                    }
                }
            }
        }
        return null;
    }
}
