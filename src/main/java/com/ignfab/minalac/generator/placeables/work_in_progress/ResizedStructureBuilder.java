package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public interface ResizedStructureBuilder {
    Structure build(int sizeX, int sizeY, int sizeZ);
    IndexMapperBuilder axisX();
    IndexMapperBuilder axisY();
    IndexMapperBuilder axisZ();


    default void checkResizability(int sizeX, int sizeY, int sizeZ) {
        if (sizeX <= 0)
            throw new RuntimeException(String.format("sizeX must be strictly positive (Asked : %d)", sizeX));
        if (sizeY <= 0)
            throw new RuntimeException(String.format("sizeY must be strictly positive (Asked : %d)", sizeY));
        if (sizeZ <= 0)
            throw new RuntimeException(String.format("sizeZ must be strictly positive (Asked : %d)", sizeZ));
        if (axisX().ask(sizeX) != sizeX)
            throw new RuntimeException(String.format("Asked sizeX (%d) do not match the allowed (%d)", sizeX, axisX().ask(sizeX)));
        if (axisY().ask(sizeY) != sizeY)
            throw new RuntimeException(String.format("Asked sizeY (%d) do not match the allowed (%d)", sizeY, axisY().ask(sizeY)));
        if (axisZ().ask(sizeZ) != sizeZ)
            throw new RuntimeException(String.format("Asked sizeZ (%d) do not match the allowed (%d)", sizeZ, axisZ().ask(sizeZ)));
    }
}
