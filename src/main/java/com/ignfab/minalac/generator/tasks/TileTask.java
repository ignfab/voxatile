package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * A task running on a generation tile.
 */
public interface TileTask extends Task {

    @Override
    default void run() {
        run(GenerationTile.current());
    }

    /**
     * Runs task.
     *
     * @param tile tile to render into
     */
    // TODO: Tile may be passed using `GenerationTile.current()` and this method renamed `runOnTile`
    void run(GenerationTile tile);
}
