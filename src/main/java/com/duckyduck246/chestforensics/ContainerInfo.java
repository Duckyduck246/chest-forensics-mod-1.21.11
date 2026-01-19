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
import java.util.Objects;

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

    public ContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a){
        type = t;
        pos = p;
        items = i;
        tags = a;
        id = "containerId:" + type + pos.toString();
        doubleChest = false;
        total++;
    }
    
    public ContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d, BlockPos o){
        type = t;
        pos = p;
        items = i;
        tags = a;
        dir = d;
        otherPos = o;
        id = "containerId:" + type + pos.toString() + dir.toString() + otherPos.toString();
        doubleChest = true;
        total++;
    }

    public ContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, BlockPos o){
        type = t;
        pos = p;
        items = i;
        tags = a;
        otherPos = o;
        id = "containerId:" + type + pos.toString() + otherPos.toString();
        doubleChest = true;
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
                        ChestForensicsClient.LOGGER.info("slots: " + handler.slots.size());
                        for (int a = 0; a < handler.slots.size(); a++) {
                            ItemStack stack = handler.getSlot(a).getStack();
                            if (!(handler.getSlot(a).inventory instanceof net.minecraft.entity.player.PlayerInventory)) {
                                items.add(stack.copy());
                                String nameOfItem = stack.getItem().getName().getString();
                                String dataOfItem = stack.getComponents().toString();
                                int count = stack.getCount();
                                ChestForensicsClient.LOGGER.info(a + ": " + count + "x " + nameOfItem + "      (" + dataOfItem + ")");
                            }
                            else {
                                ChestForensicsClient.LOGGER.info("(handler.getSlot(a).inventory instanceof net.minecraft.entity.player.PlayerInventory)");
                            }
                        }
                        ChestForensicsClient.LOGGER.info("returned items");
                        return items;
                    }
                    else{
                        ChestForensicsClient.LOGGER.info("current screen is not an instance of handled screen");
                    }
                }
                else{
                    ChestForensicsClient.LOGGER.info("client.player or client.player.currentScreenHandler is null");
                }
                break;
            case 2:
                break;

        }
        return null;
    }

    public static ArrayList<ItemStack> compareItems(ArrayList<ItemStack> oldStack, ArrayList<ItemStack> currentStack){
        ChestForensicsClient.LOGGER.info("compareItems method called");
        ArrayList<ItemStack> diff = new ArrayList<ItemStack>();
        ChestForensicsClient.LOGGER.info("old size: " + oldStack.size());
        ChestForensicsClient.LOGGER.info("new size: " + currentStack.size());

        if(oldStack.size() >= currentStack.size()){
            for(int i = 0; i < oldStack.size(); i++){
                if(i < currentStack.size()){
                    ItemStack stackA = oldStack.get(i);
                    ItemStack stackB = currentStack.get(i);
                    int countA = stackA.getCount();
                    int countB = stackB.getCount();
                    if(!((ItemStack.areItemsAndComponentsEqual(stackA, stackB)) && (countA == countB))) {
                        if (ItemStack.areItemsAndComponentsEqual(stackA, stackB)) {
                            ChestForensicsClient.LOGGER.info(stackA.getComponents().toString());
                            ItemStack itemStack = stackA.copy();
                            itemStack.setCount(stackB.getCount() - stackA.getCount());
                            diff.add(itemStack);
                        } else {
                            ChestForensicsClient.LOGGER.info(stackA.getComponents().toString());
                            ChestForensicsClient.LOGGER.info(stackB.getComponents().toString());
                            ItemStack itemStack1 = stackA.copy();
                            itemStack1.setCount(-stackA.getCount());
                            diff.add(itemStack1);
                            ItemStack itemStack2 = stackB.copy();
                            itemStack2.setCount(stackB.getCount());
                            diff.add(itemStack2);
                        }
                    }
                }
            }
            ChestForensicsClient.LOGGER.info("returned diff: " + diff);
            return diff;
        }
        else{
            for(int i = 0; i < currentStack.size(); i++){
                if(i < oldStack.size()){
                    ItemStack stackA = oldStack.get(i);
                    ItemStack stackB = currentStack.get(i);
                    int countA = stackA.getCount();
                    int countB = stackB.getCount();
                    if(!((ItemStack.areItemsAndComponentsEqual(stackA, stackB)) && (countA == countB))) {
                        if (ItemStack.areItemsAndComponentsEqual(stackA, stackB)) {
                            ChestForensicsClient.LOGGER.info(stackA.getComponents().toString());
                            ItemStack itemStack = stackA.copy();
                            itemStack.setCount(stackB.getCount() - stackA.getCount());
                            diff.add(itemStack);
                        } else {
                            ChestForensicsClient.LOGGER.info(stackA.getComponents().toString());
                            ChestForensicsClient.LOGGER.info(stackB.getComponents().toString());
                            ItemStack itemStack1 = stackA.copy();
                            itemStack1.setCount(-stackA.getCount());
                            diff.add(itemStack1);
                            ItemStack itemStack2 = stackB.copy();
                            itemStack2.setCount(stackB.getCount());
                            diff.add(itemStack2);
                        }
                    }
                }
            }
            ChestForensicsClient.LOGGER.info("returned null");
            return diff;
        }

    }

    public static String getID(String t, BlockPos p, Direction d){
        return "containerId:" + t + p.toString() + d.toString();
    }

    public static String getID(String t, BlockPos p){
        return "containerId:" + t + p.toString();
    }

    public static String getID(String t, BlockPos p, BlockPos o){
        return "containerId:" + t + p.toString() + o.toString();
    }

    public static String getID(String t, BlockPos p, Direction d, BlockPos o){
        return "containerId:" + t + p.toString() + d.toString() + o.toString();
    }
}
