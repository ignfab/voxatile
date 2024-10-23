package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

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
     * List of layers constituting the sturcture.
     */
    @JsonSetter(nulls = Nulls.SKIP, contentNulls = Nulls.FAIL)
    public List<Layer> layers = Collections.emptyList();

    /**
     * Direction of the stack (optional, default "upwards").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Direction direction = Direction.upwards;

    /**
     * Creates a new {@code VerticalVoxelStructureParams} with required fields.
     *
     * @param layers List of layers
     */
    @ConstructorProperties({"layers"})
    public StackStructureParams(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public void validate()  throws IllegalArgumentException {
        for (Layer layer : layers)
            layer.material.validate();
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (layers == null || layers.isEmpty())
            return new NoVoxel();

        SimpleVoxelStructure structure = new SimpleVoxelStructure();
        int z = 0;
        for (Layer layer : layers) {
            // As we have only VoxelTypeParams, we should always have VoxelTypes
            Placeable placeable = layer.material.create(world);
            int count = layer.height;
            while (count > 0) {
                count--;
                structure.set(0, 0, z, placeable);
                if (direction == Direction.upwards)
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
        upwards,
        /**
         * Stack starts from reference point and goes downwards.
         */
        downwards
    };

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
