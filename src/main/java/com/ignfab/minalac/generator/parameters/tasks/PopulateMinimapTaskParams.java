package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.PopulateMinimapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link PopulateMinimapTask}.
 */
public class PopulateMinimapTaskParams extends TileTaskParams {

    @Override
    public TileTask create(Generation generation) {
        return new PopulateMinimapTask();
    }
}
