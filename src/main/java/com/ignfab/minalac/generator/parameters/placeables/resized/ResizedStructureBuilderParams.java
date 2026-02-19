package com.ignfab.minalac.generator.parameters.placeables.resized;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.resized.DefaultResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class ResizedStructureBuilderParams {
    public PlaceableStructureParams base;
    public Integer elasticAt;
    public Integer minRepetition;

    public ResizedStructureBuilder create(Seed seed) {
        PlaceableStructure structure = base.create(seed);
        ResizedStructureBuilder builder = structure.toFixedResizedBuilder();
        // return DefaultResizedStructureBuilder.stretchX(builder, elasticAt, minRepetition);
        builder = DefaultResizedStructureBuilder.stretchX(builder, elasticAt, minRepetition);
        builder = DefaultResizedStructureBuilder.repeatZ(builder, 1);
        builder = DefaultResizedStructureBuilder.repeatX(builder, 1);
        return builder;
    }
}
