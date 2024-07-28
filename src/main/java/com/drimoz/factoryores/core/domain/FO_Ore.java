package com.drimoz.factoryores.core.domain;

import net.minecraft.nbt.CompoundTag;

public record FO_Ore (int maxCount, int currentCount) {

    // Interface

    public boolean canExtractCount(int extractCount) {
        return currentCount >= extractCount;
    }
}
