package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A tiled part of the generation.
 */
public class GenerationTile {
    private final Generation generation;
    private final VoxelTile voxels;
    private final HeightmapStore heightmaps;
    private final ModelStore models = new ModelStore();

    /**
     * Creates a new {@code GenerationTile} for a generation and a volume.
     *
     * @param generation parent generation of this tile
     * @param limits limits of the tile
     */
    public GenerationTile(Generation generation, WorldBBox3d limits) {
        this.generation = generation;

        // Create voxel tile
        voxels = generation.world().newTile(limits);

        // Create heightmap store populated with stored heigthmaps
        heightmaps = new HeightmapStore(generation.heightmaps(), limits.to2d());
    }

    /**
     * Gives the generation which this tile belongs to.
     *
     * @return generation
     */
    public Generation generation() {
        return generation;
    }

    /**
     * Returns the {@link ModelStore}.
     * @return the model store
     */
    public ModelStore models() {
        return models;
    }

    /**
     * Gives the limits of the tile.
     *
     * @return limits of the tile
     */
    public WorldBBox3d limits() {
        return voxels.limits();
    }

    /**
     * Gives the {@link HeightmapStore} for this generation tile.
     *
     * @return heightmap store for the generation tile
     */
    public HeightmapStore heightmaps() {
        return heightmaps;
    }

    /**
     * Gives the {@link VoxelTile} for this generation tile.
     *
     * @return voxel world tile corresponding to the generation tile
     */
    public VoxelTile voxels() {
        return voxels;
    }

    /**
     * Saves tile undelying data to its final destination so tile could be freed.
     *
     * @throws MapWriteException if an error occurs while writing to destination.
     */
    public void save() throws MapWriteException {

        // Heightmaps are discarded, only voxels are saved
        voxels().save();
   }
}
