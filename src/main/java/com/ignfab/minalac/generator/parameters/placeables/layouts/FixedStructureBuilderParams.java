package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.builders.AxisStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class FixedStructureBuilderParams extends AxisStructureBuilderParams {
    public PlaceableStructureParams fixed;
    @Override
    public AxisStructureBuilder create(Seed seed) {
        return fixed.create(seed).toFixedResizedBuilder();
    }
}
