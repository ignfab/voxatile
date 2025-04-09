package com.ignfab.minalac.generator.tasks;

import java.util.function.Consumer;

import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A task running on a generation tile.
 */
public interface TileTask extends Consumer<VoxelTile> {

    /**
     * Runs task.
     *
     * @param tile tile to render into
     */
    void run(VoxelTile tile);

    @Override
    default void accept(VoxelTile tile) {
        run(tile);
    }
}
