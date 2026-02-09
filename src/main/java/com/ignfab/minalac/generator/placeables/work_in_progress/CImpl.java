package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class CImpl extends Coifetan {

    protected CImpl(IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder, IndexesToResizedStructureBuilder builderProvider) {
        super(axisXBuilder, axisYBuilder, axisZBuilder, builderProvider);
    }

    public static Coifetan hi(ResizedStructureBuilder edges, ResizedStructureBuilder middle) {
        int minY = Math.max(edges.axisY().minimalLength(), middle.axisY().minimalLength());
        int minZ = Math.max(edges.axisZ().minimalLength(), middle.axisZ().minimalLength());

        return new CImpl(
            new IndexMapperBuilder.MiddleTakesAll(edges.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(minY),
            new IndexMapperBuilder.Identity(minZ),
            (i, j, k) -> (i == 1) ? middle : edges
        );
    }
}

