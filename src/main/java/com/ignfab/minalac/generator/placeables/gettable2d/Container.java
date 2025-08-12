package com.ignfab.minalac.generator.placeables.gettable2d;

import java.util.ArrayList;
import java.util.List;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class Container implements Gettable2d {
    List<Layout> layouts = new ArrayList<>();

    public void add(Layout layout) {
        if(!contains(layout.bbox()))
            layouts.add(layout);
    }

    private boolean contains(WorldBBox2d bbox) {
        for (Layout layout : layouts)
            if (layout.bbox().contains(bbox))
                return true;
        return false;
    }

    @Override
    public Placeable get(int u, int v) {
        for (Layout layout : layouts)
            if (layout.bbox().contains(u, v))
                return layout.get(u, v);
        return Nothing.INSTANCE;
    }
}
