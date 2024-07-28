package com.drimoz.factoryores.tests;

import java.util.Random;

public class NoiseMap {
    private int width;
    private int height;
    private double[][] noiseMap;

    public NoiseMap(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.noiseMap = new double[width][height];
        generateNoiseMap(seed);
    }

    private void generateNoiseMap(long seed) {
        Random random = new Random(seed);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                noiseMap[x][z] = random.nextDouble();
            }
        }
    }

    public double[][] getNoiseMap() {
        return noiseMap;
    }
}
