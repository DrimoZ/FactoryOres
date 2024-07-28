package com.drimoz.factoryores.core.domain;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class FO_OrePatch {

    // Private Properties

    private final Block ore;

    private final ColumnPos center;
    private final HashMap<ColumnPos, FO_Ore> positionMap;

    private final int baseRichness;
    private int currentRichness;

    // Serialization

    public static FO_OrePatch load(CompoundTag nbt) {
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("ore")));
        ColumnPos center = new ColumnPos(ColumnPos.getX(nbt.getLong("center")), ColumnPos.getZ(nbt.getLong("center")));

        HashMap<ColumnPos, FO_Ore> positionMap = new HashMap<>();
        ListTag positionMapNBT = nbt.getList("position_map",ListTag.TAG_COMPOUND);
        for (Tag tag : positionMapNBT) {
            CompoundTag pairNBT = (CompoundTag) tag;
            long columnPosLong = pairNBT.getLong("column_pos"); // Deserialize ColumnPos from long
            ColumnPos key = new ColumnPos(ColumnPos.getX(columnPosLong), ColumnPos.getZ(columnPosLong));
            int maxCount = pairNBT.getInt("max_count");
            int currentCount = pairNBT.getInt("current_count");
            positionMap.put(key, new FO_Ore(maxCount, currentCount));
        }

        int baseRichness = nbt.getInt("base_richness");
        int currentRichness = nbt.getInt("current_richness");

        return new FO_OrePatch(ore, center, positionMap, baseRichness, currentRichness);
    }


    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("ore", this.ore.getDescriptionId());
        nbt.putLong("center", this.center.toLong());

        ListTag positionMapNBT = new ListTag();
        for (Map.Entry<ColumnPos, FO_Ore> entry : this.positionMap.entrySet()) {
            CompoundTag pairNBT = new CompoundTag();
            pairNBT.putLong("column_pos", entry.getKey().toLong());
            pairNBT.putInt("max_count", entry.getValue().maxCount());
            pairNBT.putInt("current_count", entry.getValue().currentCount());
            positionMapNBT.add(pairNBT);
        }
        nbt.put("position_map", positionMapNBT);

        nbt.putInt("base_richness", this.baseRichness);
        nbt.putInt("current_richness", this.currentRichness);

        return nbt;
    }

    // Life Cycle

    private FO_OrePatch(
            Block patchOre,
            ColumnPos patchCenter, HashMap<ColumnPos, FO_Ore> patchOrePositionMap,
            int baseRichness, int currentRichness
    ) {
        if (patchOre == null) throw new IllegalArgumentException( this + " - Patch must have a block.");
        if (patchCenter == null) throw new IllegalArgumentException( this + " - Patch must have a center.");
        if (!patchOrePositionMap.containsKey(patchCenter)) throw new IllegalArgumentException( this + " - patchOrePositionMap must contain patchCenter.");

        this.ore = patchOre;
        this.center = patchCenter;
        this.positionMap = patchOrePositionMap;
        this.baseRichness = baseRichness;
        this.currentRichness = currentRichness;
    }

    public FO_OrePatch(
            Block patchOre,
            ColumnPos patchCenter, HashMap<ColumnPos, FO_Ore> patchOrePositionMap
    ) {
        if (patchOre == null) throw new IllegalArgumentException( this + " - Patch must have a block.");
        if (patchCenter == null) throw new IllegalArgumentException( this + " - Patch must have a center.");
        if (!patchOrePositionMap.containsKey(patchCenter)) throw new IllegalArgumentException( this + " - patchOrePositionMap must contain patchCenter.");

        this.ore = patchOre;
        this.center = patchCenter;
        this.positionMap = patchOrePositionMap;

        this.baseRichness = this.positionMap.values().stream()
                .mapToInt(FO_Ore::maxCount)
                .sum();
        this.currentRichness = this.positionMap.values().stream()
                .mapToInt(FO_Ore::currentCount)
                .sum();
    }

    // Interface

    public FO_Ore oresAtPosition(ColumnPos position) {
        if (!containsPosition(position)) return null;

        return this.positionMap.get(position);
    }

    public boolean extractAtPosition(ColumnPos orePosition, int count, boolean simulate) {
        if (!containsPosition(orePosition)) return false;

        FO_Ore ore = this.positionMap.get(orePosition);

        if (!ore.canExtractCount(count)) return false;

        if (simulate) return true;

        ore = new FO_Ore(ore.maxCount(), ore.currentCount() - count);
        this.positionMap.put(orePosition, ore);
        this.currentRichness -= count;

        return true;
    }

    public boolean containsPosition(ColumnPos orePosition) {
        return this.positionMap.containsKey(orePosition);
    }

    // TODO : EQUALS !


    // Getters

    public Block getPatchOre() {
        return ore;
    }

    public ColumnPos getPatchCenter() {
        return center;
    }

    public HashMap<ColumnPos, FO_Ore> getPatchPositionMap() {
        return positionMap;
    }

    public int getPatchBaseRichness() {
        return baseRichness;
    }

    public int getPatchCurrentRichness() {
        return currentRichness;
    }
}
