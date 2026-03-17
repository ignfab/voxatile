package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class FixedStructureBuilderParams extends LayoutBuilderParams {
    public PlaceableStructureParams fixed;
    @Override
    public LayoutBuilder create(Seed seed) {
        return fixed.create(seed).toLayoutBuilder();
    }
}
