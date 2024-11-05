package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

public interface ReadableHeightmap {

    int get(int x, int y);

    default int get(WorldCoords2d position) {
        return get(position.x(), position.y());
    }
}
