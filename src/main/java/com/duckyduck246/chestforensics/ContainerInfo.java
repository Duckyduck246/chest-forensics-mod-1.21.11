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
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

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
        id = "containerId:" + type + pos.toString() + dir.toString();
        doubleChest = true;
        otherPos = o;
        total++;
    }
}
