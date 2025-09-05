package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A fake GenerationTile over a {@link TestingVoxelWorld}.
 *
 * For now, used {@link TestingVoxelWorld} has no seed nor CRS.
 */
public class TestingGenerationTile extends GenerationTile {

    /**
     * A default {@code TestingGenerationTile} instance to pass as argument where it won't actually be used.
     */
    public static final TestingGenerationTile UNUSED = new TestingGenerationTile(TestingVoxelWorld.UNUSED, WorldBBox3d.EMPTY);

    // This is a mock heightmap store that is re-created each {@code newStoredHeightmap} call.
    private final TestingHeightmapStore heightmaps;

    private TestingGenerationTile(TestingVoxelWorld world, WorldBBox3d limits) {
        super(new Generation(
                world,
                null, // Seed
                null, // CRS
                0.0, // CenterX
                0.0, // CenterY
                limits.sizeX(), // extentX
                limits.sizeY(), // extentY
                1.0, // Horizontal scale
                1.0, // Vertical sclae
                0, // Angle
                Math.max(limits.sizeX(), limits.sizeY()) // Max tile size
            ),
            limits);
        heightmaps = new TestingHeightmapStore(generation().heightmaps(), limits().to2d());
    }

    /**
     * Creates a new {@code TestingGenerationTile}.
     *
     * @param limits Limits of that tile
     */
    public TestingGenerationTile(WorldBBox3d limits) {
        this(new TestingVoxelWorld(), limits);
    }

    @Override
    public TestingVoxelTile voxels() {
        return (TestingVoxelTile) super.voxels();
    }

    /**
     * Creates a new {@link TestingHeightmap} for this tile.
     *
     * @param name Name of that heightmap (allows it to be stored)
     * @param bbox Area of that heightmap
     * @param defaultValue Default value for this heightmap
     *
     * @return the created {@link TestingHeightmap}.
     */
    public TestingHeightmap newStoredHeightmap(String name, WorldBBox2d bbox, int defaultValue) {
        TestingHeightmap heightmap = new TestingHeightmap(name, bbox, defaultValue);
        generation().heightmaps().add(heightmap.declaration());
        heightmaps.addHeightmap(heightmap);
        return heightmap;
    }

    /**
     * Creates a new {@link TestingHeightmap} for this tile. Created heightmap will have same size as tile.
     *
     * @param name Name of that heightmap (allows it to be stored)
     * @param defaultValue Default value for this heightmap
     *
     * @return the created {@link TestingHeightmap}.
     */
    public TestingHeightmap newStoredHeightmap(String name, int defaultValue) {
        return newStoredHeightmap(name, limits().to2d(), defaultValue);
    }

    /**
     * Gives the mocked {@link HeightmapStore} for this testing generation tile.
     *
     * @return heightmap store
     */
    public HeightmapStore heightmaps() {
        return heightmaps;
    }

    private class TestingHeightmapStore extends HeightmapStore {
        TestingHeightmapStore(HeightmapDeclarationStore heightmaps, WorldBBox2d bbox) {
            super(heightmaps, bbox);
        }

        public void addHeightmap(TestingHeightmap heightmap) {
            heightmaps.put(heightmap.spec(), heightmap);
        }
    }
}

