package com.ignfab.minalac.generator.tasks;

import java.util.function.Consumer;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A task running on a generation tile.
 */
public interface TileTask extends Consumer<WorldBBox3d> {
    /**
     * Runs task.
     *
     * @param bbox generation tile limits
     */
    void run(WorldBBox3d bbox);

    @Override
    default void accept(WorldBBox3d bbox) {
        run(bbox);
    }
}
