package com.ignfab.minalac.generator.generation;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A fake Generation over a {@link TestingVoxelWorld}.
 */
public class TestingGeneration extends Generation {
    /**
     * A generation to use for tests when needed but not used.
     * It has no CRS and a {@linkplain WorldBBox3d#ORIGIN 1x1x1 bounding box}.
     * <p>
     * Careful: Only use this when the generation object is truly unused! This instance is shared and not immutable!
     * @see TestingGeneration#TestingGeneration()
     */
    public static final TestingGeneration UNUSED = new TestingGeneration();

    /**
     * Creates a new {@code TestingGeneration}.
     * It has no CRS and a {@linkplain WorldBBox3d#ORIGIN 1x1x1 bounding box}.
     */
    public TestingGeneration() {
        this(WorldBBox3d.ORIGIN, null);
    }

    /**
     * Creates a new {@code TestingGeneration}.
     * It has not CRS.
     * @param limits Limits of that generation
     */
    public TestingGeneration(WorldBBox3d limits) {
        this(limits, null);
    }

    /**
     * Creates a new {@code TestingGeneration}.
     * It has a {@linkplain WorldBBox3d#ORIGIN 1x1x1 bounding box}.
     * @param crs Coordinate reference system to use
     */
    public TestingGeneration(CoordinateReferenceSystem crs) {
        this(WorldBBox3d.ORIGIN, crs);
    }

    /**
     * Creates a new {@code TestingGeneration}.
     * @param limits Limits of that generation
     * @param crs Coordinate reference system to use
     */
    public TestingGeneration(WorldBBox3d limits, CoordinateReferenceSystem crs) {
        super(
            new TestingVoxelWorld(),
            new TestingSeed(""), // Seed
            crs, // CRS
            0.0, // CenterX
            0.0, // CenterY
            limits.sizeX(), // ExtentX
            limits.sizeY(), // ExtentY
            1.0, // Horizontal scale
            1.0, // Vertical scale
            0, // Angle
            Math.max(limits.sizeX(), limits.sizeY())
        );
    }

    @Override
    public TestingSeed seed() {
        return (TestingSeed) super.seed();
    }
}
