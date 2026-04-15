package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.HeightmapDeclarationParams;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.Surfer2PlaygroundTask;
import com.ignfab.minalac.generator.tasks.TileTask;

public class Surfer2PlaygroundTaskParams extends TileTaskParams {
    public String polygon;
    public SurfaceParams surface;
    public SurfaceParams line;

    @Override
    public TileTask create(Generation generation) {
        Surfer2PlaygroundTask task =  new Surfer2PlaygroundTask(
            polygon,
            surface.placeable.create(generation.seed()),
            surface.at.create(generation.heightmaps())
        );
        task.atLine = line.at.create(generation.heightmaps());
        task.lineVoxel = line.placeable.create(generation.seed());
        return task;
    }

    public static class SurfaceParams {
        public PlaceableParams placeable;
        public ReadableHeightmapParams at;
    }
}
