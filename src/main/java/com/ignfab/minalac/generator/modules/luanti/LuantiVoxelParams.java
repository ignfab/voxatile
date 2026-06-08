package com.ignfab.minalac.generator.modules.luanti;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for Luanti voxels with only node name.
 */
public class LuantiVoxelParams extends PlaceableParams {
    /**
     * Node name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String node;

    /**
     * Node param1 (Refer to Luanti documentation).
     */
    public byte param1 = 0;

    /**
     * Node param2 (Refer to Luanti documentation).
     */
    public byte param2 = 0;

    /**
     * Creates a new {@code LuantiVoxelParams}.
     *
     * @param node Node type name
     */
    @ConstructorProperties({"node"})
    public LuantiVoxelParams(String node) {
        this.node = node;
    }

    @Override
    public void validate() {
        if (node.isBlank())
            throw new IllegalArgumentException("node should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new LuantiVoxel(node, param1, param2);
    }
}
