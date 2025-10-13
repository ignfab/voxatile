package com.ignfab.minalac.generator.modules.minecraft;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for simple Minecraft voxel with only block type name.
 */
// defaultImpl is needed because Jackson deduction cannot rely on absence of field to determine a subtype
// See this GitHub issue for more info: https://github.com/FasterXML/jackson-databind/issues/2976
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = MinecraftVoxelParams.class)
@JsonSubTypes(@JsonSubTypes.Type(MinecraftBlockEntityVoxelParams.class))
public class MinecraftVoxelParams extends PlaceableParams {
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
     * Creates a new {@code MinecraftVoxelParams}.
     *
     * @param block Block type name
     */
    @ConstructorProperties("block")
    public MinecraftVoxelParams(String block) {
        this.block = block;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (block.isBlank())
            throw new IllegalArgumentException("block should not be empty or blank");
    }

    @Override
    public MinecraftVoxel create(Seed seed) {
        return new MinecraftVoxel(block, properties);
    }

    /**
     * Creates a new {@code MinecraftVoxelParams} from packed form.
     * @param block Block in packed form
     * @return The created params
     */
    public static MinecraftVoxelParams packed(String block) {
        return new Packed(block);
    }

    private static final class Packed extends MinecraftVoxelParams {
        private Packed(String block) {
            super(block);
        }

        @Override
        public MinecraftVoxel create(Seed seed) {
            if (block.indexOf('{') == -1)
                return MinecraftVoxel.fromString(block);
            return MinecraftBlockEntityVoxel.fromString(block);
        }
    }
}
