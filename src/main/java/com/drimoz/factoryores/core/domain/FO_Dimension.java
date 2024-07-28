package com.drimoz.factoryores.core.domain;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FO_Dimension {

    // Private Properties

    private final ResourceLocation id;
    private final Set<FO_OrePatch> orePatches;
    private final Set<ChunkPos> generatedChunks;

    // Serialization

    public static FO_Dimension load(CompoundTag nbt) {
        ResourceLocation dimensionId = new ResourceLocation(nbt.getString("id"));
        Set<FO_OrePatch> orePatches = new HashSet<>();
        Set<ChunkPos> generatedChunks = new HashSet<>();

        // Deserialize ore patches
        ListTag orePatchesList = nbt.getList("ore_patches", ListTag.TAG_COMPOUND);
        for (Tag tag : orePatchesList) {
            CompoundTag orePatchTag = (CompoundTag) tag;
            FO_OrePatch orePatch = FO_OrePatch.load(orePatchTag);
            orePatches.add(orePatch);
        }

        // Deserialize generated chunks
        ListTag generatedChunksList = nbt.getList("generated_chunks", ListTag.TAG_LONG);
        for (Tag tag : generatedChunksList) {
            long chunkPosLong = ((LongTag) tag).getAsLong();
            generatedChunks.add(new ChunkPos(chunkPosLong));
        }

        return new FO_Dimension(dimensionId, orePatches, generatedChunks);
    }

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", this.id.toString());

        // Serialize ore patches
        ListTag orePatchesList = new ListTag();
        for (FO_OrePatch orePatch : this.orePatches) {
            CompoundTag orePatchTag = orePatch.save();
            orePatchesList.add(orePatchTag);
        }
        nbt.put("ore_patches", orePatchesList);

        // Serialize generated chunks
        ListTag generatedChunksList = new ListTag();
        for (ChunkPos chunkPos : this.generatedChunks) {
            generatedChunksList.add(LongTag.valueOf(chunkPos.toLong()));
        }
        nbt.put("generated_chunks", generatedChunksList);

        return nbt;
    }

    // Life Cycle

    public FO_Dimension(
            ResourceLocation dimensionId,
            Set<FO_OrePatch> dimensionOrePatches,
            Set<ChunkPos> dimensionGeneratedChunks
    ) {
        if (dimensionId == null) throw new IllegalArgumentException( this + " - Patch must have an id.");

        this.id = dimensionId;
        this.orePatches = dimensionOrePatches == null ? new HashSet<>() : dimensionOrePatches;
        this.generatedChunks = dimensionGeneratedChunks == null ? new HashSet<>() : dimensionGeneratedChunks;
    }

    // Interface

    public boolean addOrePatch(FO_OrePatch orePatch) {
        // TODO : Verify Patches !
        return this.orePatches.add(orePatch);
    }

    public boolean addGeneratedChunk(ChunkPos chunkPos) {
        // TODO : Verify Chunk !
        return this.generatedChunks.add(chunkPos);
    }

    public boolean mergeDimensions(FO_Dimension otherDimension) {
        if (!otherDimension.getDimensionId().equals(this.id)) return false;

        for (FO_OrePatch orePatch : otherDimension.orePatches) {
            addOrePatch(orePatch);
        }

        for (ChunkPos chunkPos : otherDimension.generatedChunks) {
            addGeneratedChunk(chunkPos);
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FO_Dimension that = (FO_Dimension) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Calculated Properties

    public boolean isChunkGenerated(ChunkPos chunkPos) {
        return this.generatedChunks.contains(chunkPos);
    }

    // Getters

    public ResourceLocation getDimensionId() {
        return this.id;
    }

    public Set<FO_OrePatch> getDimensionOrePatches() {
        return this.orePatches;
    }

    public Set<ChunkPos> getDimensionGeneratedChunks() {
        return this.generatedChunks;
    }
}
