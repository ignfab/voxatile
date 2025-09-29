package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * A task doing absolutely nothing (because nothing is important).
 *
 * Very conveniant to wait for other tasks and use same abstract name in subsequent {@code after} fields.
 */
public final class NoOperationTask implements TileTask {

    /**
     * This class is a singleton, use this instead of constructor.
     */
    public static final NoOperationTask INSTANCE = new NoOperationTask();

    private NoOperationTask() {
    }

    @Override
    public void run(GenerationTile tile) {
    }
}
