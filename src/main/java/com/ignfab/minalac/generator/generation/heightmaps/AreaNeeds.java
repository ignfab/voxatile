package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class AreaNeeds {
    private final Map<ReadableHeightmapSpec, WorldBBox2d> areas = new HashMap<>();

    public AreaNeeds() {}

    public AreaNeeds(WritableHeightmapSpec spec, WorldBBox2d area) {
        this();
        areas.put(spec, area);
    }

    public void forEach(BiConsumer<? super ReadableHeightmapSpec,? super WorldBBox2d> action) {
        areas.forEach(action);
    }

    public void add(ReadableHeightmapSpec spec, WorldBBox2d area) {
        WorldBBox2d existing = areas.get(spec);
        areas.put(spec, existing == null ? area : WorldBBox2d.surrounding(area, existing));
    }

    public void add(AreaNeeds needs) {
        needs.forEach(this::add);
    }

    // Helpers
    public AreaNeeds enlarged(WorldBBox2d by) {
        AreaNeeds needs = new AreaNeeds();
        areas.forEach((spec, area) -> needs.add(spec, area.enlarged(by)));
        return needs;
    }
}
