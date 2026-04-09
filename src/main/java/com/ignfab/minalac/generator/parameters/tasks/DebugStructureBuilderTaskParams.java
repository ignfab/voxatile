package com.ignfab.minalac.generator.parameters.tasks;

import java.util.List;
import java.util.stream.Collectors;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.DebugStructureBuilderTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class DebugStructureBuilderTaskParams extends TileTaskParams {
    public List<LayoutBuilderParams> build;
    public DebugPlaceParams at;
    public DebugSizeParams size;

    @Override
    public TileTask create(Generation generation) {
        try {
            return new DebugStructureBuilderTask(
                build.stream().map(builder -> {
                    try {
                        return builder.createBuilder(generation.seed());
                    } catch (UnbuildableException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).collect(Collectors.toList()),
                new WorldCoords3d(at.x, at.y, at.z),
                new WorldSize2d(size.width, size.height)
            );
        } catch (UnbuildableException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class DebugPlaceParams {
        public int x;
        public int y;
        public int z;
    }

    public static class DebugSizeParams {
        public int width;
        public int height;
    }
}
