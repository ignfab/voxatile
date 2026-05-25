package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A fake GenerationTile over a {@link TestingGeneration}.
 * <p>
 * The tile occupies the whole limits of the generation
 */
public class TestingGenerationTile extends GenerationTile {

    // This is a mock heightmap store that supports modification through {@code newStoredHeightmap} call.
    private final TestingHeightmapStore heightmaps;

    /**
     * Creates a new {@code TestingGenerationTile}.
     *
     * @param limits Limits of that tile
     */
    public TestingGenerationTile(WorldBBox3d limits) {
        super(new TestingGeneration(limits), limits);
        heightmaps = new TestingHeightmapStore(generation().heightmaps(), limits().to2d());
    }

    @Override
    public TestingGeneration generation() {
        return (TestingGeneration) super.generation();
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
    @Override
    public HeightmapStore heightmaps() {
        return heightmaps;
    }

    private static class TestingHeightmapStore extends HeightmapStore {
        TestingHeightmapStore(HeightmapDeclarationStore heightmaps, WorldBBox2d bbox) {
            super(heightmaps, bbox);
        }

        public void addHeightmap(TestingHeightmap heightmap) {
            heightmaps.put(heightmap.spec(), heightmap);
        }
    }
}

