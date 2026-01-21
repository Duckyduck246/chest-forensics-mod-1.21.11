package com.duckyduck246.chestforensics;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class ChestForensicsClient implements ClientModInitializer {
    public static final String MOD_ID = "chat-logger";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static String containerName = "not yet";
    int containerID = 0;
    public static BlockPos detectedPos;
    public static Direction facing;
    public static ArrayList<ContainerInfo> allContainers = new ArrayList<ContainerInfo>();
    static String id;
    public static ArrayList<PuedoItem> compare = new ArrayList<>();
    boolean allAir;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static Identifier dimension;

    @Override
    public void onInitializeClient(){
        LOGGER.info("Client Initialized");
        ScreenEvents.AFTER_INIT.register((minecraftClient, screen, i, i1) -> {
                containerName = screen.getTitle().getString();
                if (screen instanceof HandledScreen<?> handledScreen){
                    if (!(minecraftClient.crosshairTarget instanceof BlockHitResult blockHit)) {
                        LOGGER.info("may be opening an entity");
                        return;
                    }

                    LOGGER.info("DETECTED POS: " + detectedPos);
                    if(detectedPos == null){
                        LOGGER.info("DETECTED POS IS NULL");
                        return;
                    }

                    LOGGER.info("~~~CHEST OPENNENEND~~~");
                    MinecraftClient client = MinecraftClient.getInstance();
                    containerName = screen.getTitle().getString();
                    containerID = handledScreen.getScreenHandler().syncId;
                    ScreenHandler handler = handledScreen.getScreenHandler();


                    if (!(handler instanceof GenericContainerScreenHandler)) {
                        LOGGER.info("(handler instanceof GenericContainerScreenHandler)");
                        return;
                    }

                    GenericContainerScreenHandler g = (GenericContainerScreenHandler) handler;

                    if (g.getInventory() instanceof VehicleInventory) {
                        LOGGER.info("Detected vehicle inventory (Chest Boat/Minecart)");
                        return;
                    }

                    if (g.getInventory() instanceof Entity) {
                        LOGGER.info("may be opening an entity2");
                        return;
                    }

                    net.minecraft.text.Text screenTitley = client.currentScreen.getTitle();
                    String keyThing = "";
                    if (screenTitley.getContent() instanceof net.minecraft.text.TranslatableTextContent translatable) {
                        keyThing = translatable.getKey();
                    }
                    LOGGER.info("title: " + keyThing);
                    dimension = ContainerInfo.getDimension();
                    LOGGER.info("dimension set: " + dimension);

                    ScreenEvents.remove(screen).register(closedScreen -> {
                        LOGGER.info("~~~CHEST CLOCLOLOSOSESESED~~~");

                        LOGGER.info("Name: " + containerName);
                        LOGGER.info("ID: " + containerID);
                        if (Objects.equals(containerName, "Large Chest")) {
                            LOGGER.info("is a large chest");
                            BlockPos mainContainer;
                            BlockPos subContainer;
                            if (client.world != null) {
                                mainContainer = getMainContainer(client.world.getBlockEntity(detectedPos));
                                subContainer = getSubContainer(client.world.getBlockEntity(detectedPos));

                                LOGGER.info("Pos" + mainContainer);

                                addContainerInfo(containerName, mainContainer, ContainerInfo.listItems(2), new ArrayList<String>(), subContainer, dimension);
                            }
                            else{
                                LOGGER.info("ERROR: WORLD NOT INSTANTIATED");
                                addContainerInfo(containerName, detectedPos, ContainerInfo.listItems(2), new ArrayList<String>(), dimension);
                            }
                        }
                        else {
                            LOGGER.info("Pos" + detectedPos);
                            addContainerInfo(containerName, detectedPos, ContainerInfo.listItems(2), new ArrayList<String>(), dimension);
                        }
                        saveContainersToTXT();


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

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d, BlockPos o, Identifier b){
        String id = ContainerInfo.getID(t, p, d, o, b);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, o, b));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, o, b));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Direction d, Identifier b){
        String id = ContainerInfo.getID(t, p, d, b);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, b));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, b));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, BlockPos o, Identifier b){
        String id = ContainerInfo.getID(t, p, o, b);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, o, b));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, o, b));
    }

    public void addContainerInfo(String t, BlockPos p, ArrayList<ItemStack> i, ArrayList<String> a, Identifier b){
        String id = ContainerInfo.getID(t, p, b);
        for (int j = 0; j < allContainers.size(); j++){
            if(allContainers.get(j).id.equals(id)){
                allContainers.set(j, new ContainerInfo(t, p, i, a, b));
                return;
            }
        }
        allContainers.add(new ContainerInfo(t, p, i, a, b));
    }

    public static ArrayList<PuedoItem> getCompare(){
        MinecraftClient client = MinecraftClient.getInstance();
        ArrayList<PuedoItem> compared = new ArrayList<>();
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            ScreenHandler handler = handledScreen.getScreenHandler();
            id = "ERROR 1389843204";
            LOGGER.info("id set");
            LOGGER.info("" + Objects.requireNonNull(detectedPos));


            if (Objects.equals(containerName, "Large Chest")) {
                LOGGER.info("is a large chest");
                BlockPos mainContainer;
                BlockPos subContainer;
                if (client.world != null) {
                    mainContainer = getMainContainer(client.world.getBlockEntity(detectedPos));
                    subContainer = getSubContainer(client.world.getBlockEntity(detectedPos));
                    id = ContainerInfo.getID(containerName, mainContainer, subContainer, dimension);
                } else {
                    LOGGER.info("ERROR: WORLD NOT INSTANTIATED");
                    id = ContainerInfo.getID(containerName, detectedPos, dimension);
                }
            } else {
                id = ContainerInfo.getID(containerName, detectedPos, dimension);
            }
            LOGGER.info("got after geting id");
            for (int j = 0; j < allContainers.size(); j++) {
                if (allContainers.get(j).id.equals(id)) {
                    LOGGER.info("new stack:" + ContainerInfo.listItems(2));
                    compared = ContainerInfo.compareItems(ForensicsNbt.fromJsonString(allContainers.get(j).items), ContainerInfo.listItems(2));
                }
            }
            LOGGER.info("returned compared: " + compared);
            return compared;
        }
        return compared;
    }

    public static void saveContainersToTXT(){
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("chest-forensics");
        File file = configDir.resolve("chest_forensices_data.txt").toFile();
        file.getParentFile().mkdirs();
        try(FileWriter writer = new FileWriter(file, true)){
            writer.write("Chest Forensics Export\n");
            writer.write("World: " + getWorldId() + "\n");
            writer.write("Updated: " + java.time.LocalDateTime.now() + "\n\n");
            for(ContainerInfo container : allContainers){
                writer.write("Container Type: " + container.type  + "\n");
                writer.write("Pos: " + container.pos  + "\n");
                writer.write("ID: " + container.id  + "\n");
                writer.write("Items: ");
                for(String item : container.items){
                    writer.write("(" + item + ") ");
                }
                writer.write("\n\n");
            }
            LOGGER.info("exported to da txt");

        }
        catch (IOException e){
            LOGGER.info("broke; not exported to da txt");
        }
    }

    public static BlockPos getMainContainer(BlockEntity blockEntity){
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

    public static BlockPos getSubContainer(BlockEntity blockEntity){
        LOGGER.info("getSubContainer method called");
        if (!(blockEntity instanceof ChestBlockEntity chest)){
            LOGGER.info("not a chest...");
            return blockEntity.getPos();
        }
        ChestType type = chest.getCachedState().get(ChestBlock.CHEST_TYPE);
        if (type == ChestType.SINGLE || type == ChestType.RIGHT){
            LOGGER.info("single chest or right chest");
            return chest.getPos();
        }
        if (type == ChestType.LEFT){
            LOGGER.info("finally is a left chest");
            World world = chest.getWorld();
            if (world != null){
                Direction chestFacing = chest.getCachedState().get(ChestBlock.FACING);
                BlockPos neighbor;
                LOGGER.info(chestFacing.asString());
                switch(chestFacing){
                    case SOUTH:
                        neighbor = chest.getPos().west();
                        break;
                    case NORTH:
                        neighbor = chest.getPos().east();
                        break;
                    case EAST:
                        neighbor = chest.getPos().south();
                        break;
                    case WEST:
                        neighbor = chest.getPos().north();
                        break;
                    default:
                        neighbor = chest.getPos().up();
                        break;
                }
                BlockEntity neighborEntity = world.getBlockEntity(neighbor);
                if (neighborEntity instanceof ChestBlockEntity neighborChest){
                    ChestType neighborType = neighborChest.getCachedState().get(ChestBlock.CHEST_TYPE);
                    if (neighborType == ChestType.RIGHT){
                        LOGGER.info("neighbor is a right chest, returning pos");
                        return neighborChest.getPos();
                    }
                    else{
                        LOGGER.info("neighbor not a right chest? they were: " + neighborType.asString());
                    }
                }
                else{
                    LOGGER.info("neighbor not a chest??");
                }
            }
        }

        return blockEntity.getPos();
    }

    public static String getWorldId() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return client.getServer().getSaveProperties().getLevelName();
        } else if (client.getCurrentServerEntry() != null) {
            return client.getCurrentServerEntry().address.replace(":", "_").replace("/", "_");
        }
        return "idk_either_man";
    }

}