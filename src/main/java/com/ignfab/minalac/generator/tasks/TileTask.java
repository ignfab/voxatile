package com.ignfab.minalac.generator.tasks;

import java.util.function.Consumer;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * A task running on a generation tile.
 */
public interface TileTask extends Consumer<GenerationTile> {

    /**
     * Runs task.
     *
     * @param tile tile to render into
     */
    void run(GenerationTile tile);

    @Override
    default void accept(GenerationTile tile) {
        run(tile);
    }
}
