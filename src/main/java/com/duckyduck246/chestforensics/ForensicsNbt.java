package com.duckyduck246.chestforensics;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ForensicsNbt {

    public static ArrayList<String> toJsonString(ArrayList<ItemStack> stacks) {
        ArrayList<String> returned = new ArrayList<>();
        for (ItemStack stack : stacks) {
            DataResult<JsonElement> result = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack);
            result.result().ifPresentOrElse(
                    json -> returned.add(json.toString()),
                    () -> returned.add("{}")
            );
        }
        return returned;
    }

    public static ArrayList<ItemStack> fromJsonString(ArrayList<String> stringJson) {
        ArrayList<ItemStack> returned = new ArrayList<>();
        for (String jsonStr : stringJson) {
            try {
                JsonElement element = JsonParser.parseString(jsonStr);
                DataResult<ItemStack> result = ItemStack.CODEC.parse(JsonOps.INSTANCE, element);
                result.result().ifPresentOrElse(
                        returned::add,
                        () -> returned.add(ItemStack.EMPTY)
                );
            } catch (Exception e) {
                returned.add(ItemStack.EMPTY);            }
        }
        return returned;
    }
}
