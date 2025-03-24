package com.ignfab.minalac.generator.parameters.placeables;

import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Class for testing placeable params.
 */
public class TestingPlaceableParams extends PlaceableParams {
    /**
     * An invalid testing placeable params.
     */
    public static final TestingPlaceableParams INVALID = new TestingPlaceableParams(null);
    /**
     * A valid testing placeable params.
     */
    public static final TestingPlaceableParams VALID = new TestingPlaceableParams(NoVoxel.INSTANCE);
    private final Placeable placeable;

    /**
     * Creates a new {@code TestingVoxelTypeParams}.
     *
     * @param placeable Placeable to be returned by {@code create}.
     */
    public TestingPlaceableParams(Placeable placeable) {
        this.placeable = placeable;
    }

    @Override
    public void validate() {
        if (placeable == null)
            throw new IllegalArgumentException();
    }

    @Override
    public Placeable create(Seed seed, VoxelWorld world) {
        return placeable;
    }
}
