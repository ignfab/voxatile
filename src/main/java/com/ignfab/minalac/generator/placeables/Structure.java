package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public interface Structure extends Placeable {
    Placeable get(int x, int y, int z);
    WorldBBox3d limits();
}
