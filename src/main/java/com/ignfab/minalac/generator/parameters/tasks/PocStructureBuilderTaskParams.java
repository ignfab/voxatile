package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.PocStructureBuilderTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class PocStructureBuilderTaskParams extends TileTaskParams {
    public ReadableHeightmapParams minimum;
    public LayoutBuilderParams layout;
    public PlaceableParams fallback;
    public DebugParams placeDebug;

    @Override
    public TileTask create(Generation generation) {
        return new PocStructureBuilderTask(
            minimum.create(generation.heightmaps()),
            layout.create(generation.seed()),
            fallback.create(generation.seed()),
            new WorldCoords3d(placeDebug.at.x, placeDebug.at.y, placeDebug.at.z),
            new WorldSize3d(placeDebug.size.x, placeDebug.size.y, placeDebug.size.z)
        );
    }

    public static class DebugParams {
        public DebugPlaceParams at;
        public DebugSizeParams size;
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
