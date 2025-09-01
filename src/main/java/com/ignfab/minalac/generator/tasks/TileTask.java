package com.ignfab.minalac.generator.tasks;

import java.util.function.Consumer;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

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

    /**
     * Returns margins resulting from involved placeable or other
     * things that needs to have a greater model volume than tile volume.
     * <p>
     * Default implementation means no margin needed.
     *
     * @return margin bounding box.
     */
    default WorldBBox3d placementMargins() {
        return WorldBBox3d.EMPTY;
    };

    @Override
    default void accept(GenerationTile tile) {
        run(tile);
    }
}
