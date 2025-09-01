package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.execution.Task;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

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

}
