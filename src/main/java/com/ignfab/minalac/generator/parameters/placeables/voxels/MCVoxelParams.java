package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minecraft.MCVoxel;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for simple Minecraft voxel with only block type name.
 */
// defaultImpl is needed because Jackson deduction cannot rely on absence of field to determine a subtype
// See this GitHub issue for more info: https://github.com/FasterXML/jackson-databind/issues/2976
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = MCVoxelParams.class)
@JsonSubTypes(@JsonSubTypes.Type(MCBlockEntityVoxelParams.class))
public class MCVoxelParams extends PlaceableParams {
    /**
     * Block type name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String block;

    /**
     * Block properties (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Map<String, String> properties = null;

    /**
     * Creates a new {@code MCVoxelParams}.
     *
     * @param block Block type name
     */
    @ConstructorProperties({"block"})
    public MCVoxelParams(String block) {
        this.block = block;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (block.isBlank())
            throw new IllegalArgumentException("block should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new MCVoxel(block, properties);
    }

    // TODO probably find a better name
    public static MCVoxelParams packed(String block) {
        return new Packed(block);
    }

    private static final class Packed extends MCVoxelParams {
        private Packed(String block) {
            super(block);
        }

        @Override
        public Placeable create(Seed seed) {
            return MCVoxel.fromString(block);
        }
    }
}
