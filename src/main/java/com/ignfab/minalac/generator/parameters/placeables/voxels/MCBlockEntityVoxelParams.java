package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.github.ensgijs.nbt.io.TextNbtHelpers;
import io.github.ensgijs.nbt.tag.CompoundTag;

import com.ignfab.minalac.generator.outputs.minecraft.MCBlockEntityVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for Minecraft block entity voxel.
 */
public class MCBlockEntityVoxelParams extends MCVoxelParams {
    /**
     * Block entity ID (required).
     * Special case: If value is {@code "true"}, block type name will be used.
     * This is needed to determine that a block entity is wanted.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String blockEntity;

    /**
     * Block state data (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String dataTags = null;

    /**
     * Creates a new {@code MCBlockEntityVoxelParams}.
     *
     * @param block Block type name
     * @param blockEntity Block entity ID
     */
    @ConstructorProperties({ "block", "blockEntity" })
    public MCBlockEntityVoxelParams(String block, String blockEntity) {
        super(block);
        this.blockEntity = blockEntity.equals("true") ? block : blockEntity;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if (blockEntity.isBlank())
            throw new IllegalArgumentException("blockEntity should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        CompoundTag data;
        if (dataTags == null)
            data = null;
        else {
            try {
                data = TextNbtHelpers.fromTextNbt(dataTags).getTagAutoCast();
            } catch (IOException e) {
                throw new IllegalArgumentException("Malformed data tags: " + dataTags, e);
            }
        }
        return new MCBlockEntityVoxel(block, blockEntity, properties, data);
    }
}
