package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.resized.ResizedStructureBuilderParams;
import com.ignfab.minalac.generator.tasks.TestingStructureBuilderTask;
import com.ignfab.minalac.generator.tasks.TileTask;

public class TestingStructureBuilderTaskParams extends TileTaskParams {
    public ReadableHeightmapParams minimum;
    public ResizedStructureBuilderParams resizable;
    public PlaceableParams fallback;
    @Override
    public TileTask create(Generation generation) {
        return new TestingStructureBuilderTask(
            minimum.create(generation.heightmaps()),
            resizable.create(generation.seed()),
            fallback.create(generation.seed())
        );
    }
}
