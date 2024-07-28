package com.drimoz.factoryores;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fml.common.Mod;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = FactoryOres.MOD_ID)
public class FakeOrePatchGenerator {
    public static JsonObject loadConfig(String path) {
        try (FileReader reader = new FileReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            FactoryOres.LOGGER.error("Failed to load configuration file", e);
            return null;
        }
    }

    public static void generateFakeOrePatches(ServerLevel serverLevel, FakeOreData data, JsonObject config) {
        // Example structure, customize as needed
        int patchesPerChunk = config.get("patches_per_chunk").getAsInt();
        int maxOresPerPatch = config.get("max_ores_per_patch").getAsInt();
        Random random = new Random();

        for (int chunkX = serverLevel.getMinBuildHeight(); chunkX <= serverLevel.getMaxBuildHeight(); chunkX++) {
            for (int chunkZ = serverLevel.getMinBuildHeight(); chunkZ <= serverLevel.getMaxBuildHeight(); chunkZ++) {
                for (int i = 0; i < patchesPerChunk; i++) {
                    int oreX = chunkX * 16 + random.nextInt(16);
                    int oreY = random.nextInt(256);
                    int oreZ = chunkZ * 16 + random.nextInt(16);
                    int oreCount = random.nextInt(maxOresPerPatch) + 1;

                    data.addFakeOrePatch(oreX, oreY, oreZ, oreCount);
                }
            }
        }
    }

    public static class FakeOreData extends SavedData {
        public static final String DATA_NAME = "fake_ore_data";
        private final List<OrePatch> orePatches = new ArrayList<>();

        public FakeOreData() {
            // No-arg constructor
        }

        public static FakeOreData load(CompoundTag nbt) {
            FakeOreData data = new FakeOreData();
            // Deserialize your ore patches here from NBT
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag compound) {
            // Serialize your ore patches here to NBT
            return compound;
        }

        public void addFakeOrePatch(int x, int y, int z, int count) {
            orePatches.add(new OrePatch(x, y, z, count));
        }

        public OrePatch getPatchAt(BlockPos pos) {
            return orePatches.stream().filter(orePatch -> orePatch.isCorrectPatch(pos.getX(), pos.getY(), pos.getZ())).findFirst().orElse(null);
        }

        public static class OrePatch {
            private final int x, y, z, count;

            public OrePatch(int x, int y, int z, int count) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.count = count;
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getZ() { return z; }
            public int getCount() { return count; }

            public boolean isCorrectPatch(int x, int y, int z) {
                return x == this.x && y == this.y && z == this.z;
            }

            public int getInitialCount() {
                return count;
            }

            public int getCurrentCount() {
                return count;
            }
        }
    }
}
