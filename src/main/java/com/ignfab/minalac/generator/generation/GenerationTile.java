package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * A tiled part of the generation.
 */
public class GenerationTile {
    private VoxelWorldTile worldTile;
    private Store<Heightmap> heightmaps = new Store<>();

    /**
     * Creates a new {@code GenerationTile} for a generation and a volume.
     *
     * @param generation parent generation of this tile
     * @param limits limits of the tile
     */
    public GenerationTile(Generation generation, WorldBBox3d limits) {
        this.worldTile = generation.world().newTile(limits);
        for (String name : generation.heightmaps().keys())
            this.heightmaps.add(name, generation.heightmaps().get(name).createHeightmap(worldTile.limits().to2d()));
    }

    /**
     * Gives the limits of the tile.
     *
     * @return limits of the tile
     */
    public WorldBBox3d limits() {
        return worldTile.limits();
    }

    /**
     * Gives the "stored" heightmap store for this tile.
     *
     * @return heightmap store
     */
    public Store<Heightmap> heightmaps() {
        return heightmaps;
    }

    /**
     * Gives the {@link VoxelWorldTile} for this generation tile.
     *
     * @return voxel world tile corresponding to the generation tile
     */
    public VoxelWorldTile worldTile() {
        return worldTile;
    }

}
