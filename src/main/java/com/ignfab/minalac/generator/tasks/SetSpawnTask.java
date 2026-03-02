package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * This task sets {@link com.ignfab.minalac.generator.world.VoxelWorldMetadata} spawn position.
 * It queries the heightmap to determine the altitude of the spawn point.
 * */
public class SetSpawnTask extends TileTask {
    private final ReadableHeightmapSpec heightmapSpec;
    private final WorldCoords2d spawn;

    /**
     * Creates a new {@code SetSpawnTask}.
     *
     * @param heightmapSpec the heightmap spec to use to determine the altitude
     * @param spawn the spawn 2d position
     */
    public SetSpawnTask(ReadableHeightmapSpec heightmapSpec, WorldCoords2d spawn) {
        this.heightmapSpec = heightmapSpec;
        this.spawn = spawn;
    }

    @Override
    public void run(GenerationTile tile) {
        if (tile.limits().bbox().to2d().contains(spawn)) {
            int spawnZ = tile.heightmap(heightmapSpec).get(spawn);
            tile.generation().world().getMetadata().setSpawn(spawn.to3d(spawnZ));
        }
    }
}
