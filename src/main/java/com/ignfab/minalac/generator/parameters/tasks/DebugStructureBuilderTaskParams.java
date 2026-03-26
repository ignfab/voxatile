package com.ignfab.minalac.generator.parameters.tasks;

import java.util.List;
import java.util.stream.Collectors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.DebugStructureBuilderTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class DebugStructureBuilderTaskParams extends TileTaskParams {
    public List<LayoutBuilderParams> build;
    public DebugPlaceParams at;
    public DebugSizeParams size;
    @Override
    public TileTask create(Generation generation) {
        return new DebugStructureBuilderTask(
            build.stream().map(builder -> builder.createBuilder(generation.seed())).collect(Collectors.toList()),
            new WorldCoords3d(at.x, at.y, at.z),
            new WorldSize3d(size.x, size.y, size.z)
        );
    }

    public static class DebugPlaceParams {
        public int x;
        public int y;
        public int z;
    }

    public static class DebugSizeParams {
        public int x;
        public int y;
        public int z;
    }
}
