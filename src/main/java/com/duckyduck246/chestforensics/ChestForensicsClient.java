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
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

public class ChestForensicsClient implements ClientModInitializer {
    public static final String MOD_ID = "chat-logger";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    String containerName = "not yet";
    int containerID = 0;
    public static BlockPos detectedPos;
    public static Direction facing;
    public static ArrayList<ContainerInfo> allContainers = new ArrayList<ContainerInfo>();
    String id;
    public static ArrayList<PuedoItem> compare = new ArrayList<>();

    @Override
    public void onInitializeClient(){
        LOGGER.info("Client Initialized");

        ScreenEvents.AFTER_INIT.register((minecraftClient, screen, i, i1) -> {
            LOGGER.info("~~~CHEST OPENNENEND~~~");
                if (screen instanceof HandledScreen<?> handledScreen){
                    MinecraftClient client = MinecraftClient.getInstance();
                    containerName = screen.getTitle().getString();
                    containerID = handledScreen.getScreenHandler().syncId;
                    ScreenHandler handler = handledScreen.getScreenHandler();
                    if (!(handler instanceof GenericContainerScreenHandler)) {
                        return;
                    }

                    id = "ERROR 1389843204";
                    LOGGER.info("id set");
                    LOGGER.info("" + Objects.requireNonNull(detectedPos));
                    MinecraftClient.getInstance().execute(() -> {

                        if (Objects.equals(containerName, "Large Chest")) {
                            LOGGER.info("is a large chest");
                            BlockPos mainContainer;
                            if (client.world != null) {
                                mainContainer = getMainContainer(client.world.getBlockEntity(detectedPos));
                                id = ContainerInfo.getID(containerName, mainContainer, detectedPos);
                            } else {
                                LOGGER.info("ERROR: WORLD NOT INSTANTIATED");
                                id = ContainerInfo.getID(containerName, detectedPos);
                            }
                        } else {
                            id = ContainerInfo.getID(containerName, detectedPos);
                        }
                        LOGGER.info("got after geting id");
                        for (int j = 0; j < allContainers.size(); j++) {
                            if (allContainers.get(j).id.equals(id)) {
                                LOGGER.info("new stack:" + ContainerInfo.listItems(1));
                                compare = ContainerInfo.compareItems(allContainers.get(j).items, ContainerInfo.listItems(1));
                            }
                        }
                        for (int o = 0; o < compare.size(); o++) {
                            LOGGER.info("Compared: " + compare.get(o).getString());
                        }

                    });
                    ScreenEvents.remove(screen).register(closedScreen -> {
                        LOGGER.info("~~~CHEST CLOCLOLOSOSESESED~~~");
                        LOGGER.info("Name: " + containerName);
                        LOGGER.info("ID: " + containerID);
                        if (Objects.equals(containerName, "Large Chest")) {
                            LOGGER.info("is a large chest");
                            BlockPos mainContainer;
                            if (client.world != null) {
                                mainContainer = getMainContainer(client.world.getBlockEntity(detectedPos));
                                LOGGER.info("Pos" + mainContainer);

                                addContainerInfo(containerName, mainContainer, ContainerInfo.listItems(1), new ArrayList<String>(), detectedPos);;
                            }
                            else{
                                LOGGER.info("ERROR: WORLD NOT INSTANTIATED");
                                addContainerInfo(containerName, detectedPos, ContainerInfo.listItems(1), new ArrayList<String>());
                            }
                        }
                        else {
                            LOGGER.info("Pos" + detectedPos);
                            addContainerInfo(containerName, detectedPos, ContainerInfo.listItems(1), new ArrayList<String>());
                        }
                        detectedPos = null;

                    });
               }
        });



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

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d, BlockPos o){
        String id = ContainerInfo.getID(t, p, d);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, o));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, o));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d){
        String id = ContainerInfo.getID(t, p, d);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, BlockPos o){
        String id = ContainerInfo.getID(t, p);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, o));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, o));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a){
        String id = ContainerInfo.getID(t, p);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a));
    }

    public BlockPos getMainContainer(BlockEntity blockEntity){
        LOGGER.info("getMainContainer method called");
        if (!(blockEntity instanceof ChestBlockEntity chest)){
            LOGGER.info("not a chest");
            return blockEntity.getPos();
        }
        ChestType type = chest.getCachedState().get(ChestBlock.CHEST_TYPE);
        if (type == ChestType.SINGLE || type == ChestType.LEFT){
            LOGGER.info("single chest or left chest");
            return chest.getPos();
        }
        if (type == ChestType.RIGHT){
            LOGGER.info("finnally is a right chest");
            World world = chest.getWorld();
            if (world != null){
                Direction chestFacing = chest.getCachedState().get(ChestBlock.FACING);
                BlockPos neighbor;
                LOGGER.info(chestFacing.asString());
                switch(chestFacing){
                    case NORTH: 
                        neighbor = chest.getPos().west();
                        break;
                    case SOUTH:
                        neighbor = chest.getPos().east();
                        break;
                    case WEST:
                        neighbor = chest.getPos().south();
                        break;
                    case EAST:
                        neighbor = chest.getPos().north();
                        break;
                    default:
                        neighbor = chest.getPos().up();
                        break;
                }
                BlockEntity neighborEntity = world.getBlockEntity(neighbor);
                if (neighborEntity instanceof ChestBlockEntity neighborChest){
                    ChestType neighborType = neighborChest.getCachedState().get(ChestBlock.CHEST_TYPE);
                    if (neighborType == ChestType.LEFT){
                        LOGGER.info("neighbor is a left chest, returning pos");
                        return neighborChest.getPos();
                    }
                    else{
                        LOGGER.info("neighbor not a left chest? they were: " + neighborType.asString());
                    }
                }
                else{
                    LOGGER.info("neighbor not a chest?");
                }
            }
        }

        return blockEntity.getPos();
    }


}