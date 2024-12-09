package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.parameters.placeables.CustomPlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.world.NoVoxel;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.SimpleVoxelStructure;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A simple structure made out of stacked voxels.
 */
@SuppressWarnings("VisibilityModifier")
public class StackStructureParams extends CustomPlaceableParams {
    /**
     * List of layers constituting the structure.
     */
    @JsonSetter(nulls = Nulls.SKIP, contentNulls = Nulls.FAIL)
    public List<Layer> layers = Collections.emptyList();

    /**
     * Direction of the stack (optional, default "upwards").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Direction direction = Direction.UPWARDS;

    /**
     * Creates a new {@code StackStructureParams} with required fields.
     *
     * @param layers List of layers
     */
    @ConstructorProperties({"layers"})
    public StackStructureParams(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        for (Layer layer : layers)
            layer.material.validate();
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (layers == null || layers.isEmpty())
            return NoVoxel.INSTANCE;

        SimpleVoxelStructure structure = new SimpleVoxelStructure();
        int z = 0;
        for (Layer layer : layers) {
            Placeable placeable = layer.material.create(world);
            int count = layer.height;
            while (count > 0) {
                count--;
                structure.set(0, 0, z, placeable);
                if (direction == Direction.UPWARDS)
                    z++;
                else
                    z--;
            }
        }
        return structure;
    }

    /**
     * Stack direction.
     */
    public enum Direction {
        /**
         * Stack starts from reference point and goes upwards.
         */
        @JsonProperty("upwards")
        UPWARDS,
        /**
         * Stack starts from reference point and goes downwards.
         */
        @JsonProperty("downwards")
        DOWNWARDS
    }

    /**
     * A layer of voxels.
     */
    public static class Layer {
        /**
         * Voxel type.
         */
        public PlaceableParams material;
        /**
         * Height of the layer.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int height = 1;
    }
}
