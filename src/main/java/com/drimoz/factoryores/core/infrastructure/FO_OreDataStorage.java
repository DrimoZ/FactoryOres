package com.drimoz.factoryores.core.infrastructure;

import com.drimoz.factoryores.core.domain.FO_Dimension;
import com.drimoz.factoryores.core.domain.FO_OrePatch;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class FO_OreDataStorage {

    // Static Properties

    private static final FO_OreDataStorage INSTANCE = new FO_OreDataStorage();

    public static final String DIMENSION_TAG_NAME = "dimensions";

    // Private Properties

    private final Set<FO_Dimension> dimensions = new HashSet<>();

    // Public Properties



    // Interface ( Serialization )

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        ListTag dimList = new ListTag();

        for(FO_Dimension dimension : this.dimensions) {
            dimList.add(dimension.save());
        }

        nbt.put(DIMENSION_TAG_NAME, dimList);

        return nbt;
    }

    public void load(CompoundTag nbt) {
        if (nbt.isEmpty() || !nbt.contains(DIMENSION_TAG_NAME)) throw new IllegalArgumentException("Missing " + DIMENSION_TAG_NAME + " in Tag.");

        for(Tag dimensionTag : nbt.getList(DIMENSION_TAG_NAME, Tag.TAG_LIST)) {
            if (!(dimensionTag instanceof CompoundTag)) return;
            addDimension(FO_Dimension.load((CompoundTag) dimensionTag));
        }
    }

    // Interface

    public FO_OrePatch getPlayerOrePatch(Level level, BlockPos pos) {
        // Find correct FO_Dimension based on Level and FO_Dimension#id
        FO_Dimension playerDim = dimensions.stream().filter(dimension ->
            dimension.getDimensionId().toString().equals(level.dimension().location().toString())
        ).findFirst().orElse(null);

        if (playerDim == null) return null;

        // Call Method to find OrePatch based on ColumnPos in FO_Dimension
        return playerDim.findOrePatch(new ColumnPos(pos.getX(), pos.getZ()));
    }

    // Inner work

    private boolean addDimension(FO_Dimension dimension) {
        FO_Dimension existingDimension = this.dimensions.stream().filter(d -> d.getDimensionId().equals(dimension.getDimensionId())).findFirst().orElse(null);

        if (existingDimension == null) {
            return this.dimensions.add(dimension);
        }
        else {
            return existingDimension.mergeDimensions(dimension);
        }
    }

    // Getters

    public static FO_OreDataStorage getInstance() {
        return INSTANCE;
    }

    public Set<FO_Dimension> getDimensions() {
        return this.dimensions;
    }
}
