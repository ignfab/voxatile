package com.ignfab.minalac.generator.parameters.placeables.resized;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class FixedStructureBuilderParams extends ResizedStructureBuilderParams {
    public PlaceableStructureParams fixed;
    @Override
    public ResizedStructureBuilder create(Seed seed) {
        return fixed.create(seed).toFixedResizedBuilder();
    }
}
