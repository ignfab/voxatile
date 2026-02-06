package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.hytale.HytaleVoxel;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

public class HytaleVoxelParams extends PlaceableParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public String blockTypeKey;

    /**
     * Creates a new {@code HytaleVoxelParams}.
     *
     * @param blockTypeKey Block type key
     */
    @ConstructorProperties("blockTypeKey")
    public HytaleVoxelParams(String blockTypeKey) {
        this.blockTypeKey = blockTypeKey;
    }

    @Override
    public void validate() {
        if (blockTypeKey.isBlank())
            throw new IllegalArgumentException("blockTypeKey should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new HytaleVoxel(blockTypeKey);
    }
}
