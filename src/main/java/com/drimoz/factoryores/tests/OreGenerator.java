package com.drimoz.factoryores.tests;

import com.drimoz.factoryores.FactoryOres;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class OreGenerator {
    private final int width;
    private final int height;
    private final Set<BlockPos> occupiedBlocks;
    private final ServerLevel world;
    private static final int ORE_Y_LEVEL = -60;

    public OreGenerator(ServerLevel world, int width, int height) {
        this.width = width;
        this.height = height;
        this.occupiedBlocks = new HashSet<>();
        this.world = world;
    }

    public List<int[]> applyThreshold(double[][] noiseMap, double threshold) {
        List<int[]> patchLocations = new ArrayList<>();
        for (int x = 0; x < noiseMap.length; x++) {
            for (int z = 0; z < noiseMap[0].length; z++) {
                if (noiseMap[x][z] > threshold) {
                    patchLocations.add(new int[]{x, z});
                }
            }
        }
        return patchLocations;
    }

    private int[][] generateBlob(int radius, Random random) {
        int size = radius * 2 + 1;
        int[][] blob = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                blob[x][z] = random.nextBoolean() ? 1 : 0;
            }
        }

        for (int i = 0; i < 3; i++) { // Apply cellular automata rules for smoothing
            int[][] newBlob = new int[size][size];
            for (int x = 1; x < size - 1; x++) {
                for (int z = 1; z < size - 1; z++) {
                    int count = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (blob[x + dx][z + dz] == 1) {
                                count++;
                            }
                        }
                    }
                    newBlob[x][z] = count >= 5 ? 1 : 0;
                }
            }
            blob = newBlob;
        }

        return blob;
    }

    public void placeOreBlocks(List<int[]> patchLocations, Block oreBlock, int radius, int richness) {
        Random random = new Random();
        for (int[] loc : patchLocations) {
            int[][] blob = generateBlob(radius, random);

            int baseX = loc[0] - radius;
            int baseZ = loc[1] - radius;

            for (int dx = 0; dx < blob.length; dx++) {
                for (int dz = 0; dz < blob[0].length; dz++) {
                    if (blob[dx][dz] == 1) {
                        int x = baseX + dx;
                        int z = baseZ + dz;
                        if (isWithinBounds(x, z)) {
                            BlockPos pos = new BlockPos(x, ORE_Y_LEVEL, z);
                            if (occupiedBlocks.add(pos)) {
                                placeOreInWorld(pos, oreBlock, richness);
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean isWithinBounds(int x, int z) {
        return x >= 0 && x < width && z >= 0 && z < height;
    }

    private void placeOreInWorld(BlockPos pos, Block oreBlock, int richness) {
        for (int i = 0; i < richness; i++) {
            world.setBlock(pos, oreBlock.defaultBlockState(), 3);
            // FactoryOres.LOGGER.info("Placed {} ore at ({}, {}, {}) with richness {}", oreBlock, pos.getX(), pos.getY(), pos.getZ(), richness);
        }
    }
}
