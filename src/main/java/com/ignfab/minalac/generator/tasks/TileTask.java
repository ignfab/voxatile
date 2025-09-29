package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * A task running on a generation tile.
 */
public abstract class TileTask implements Task {


    @Override
    public final void run() {
        if (GenerationTile.current() == null)
            // TODO: Warn about that
            return;

        run(GenerationTile.current());
    }

    /**
     * Runs task.
     *
     * @param tile tile to render into
     */
    // TODO: Tile may be passed using `GenerationTile.current()` and this method renamed `runOnTile`
    public abstract void run(GenerationTile tile);
}
