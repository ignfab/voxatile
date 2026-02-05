package com.ignfab.minalac.generator.placeables.resized;

import com.ignfab.minalac.generator.placeables.Structure;

public interface ResizedStructureBuilder {
    Structure build(int sizeX, int sizeY, int sizeZ) throws UnresizableStructureException;
    IndexMapperBuilder axisX();
    IndexMapperBuilder axisY();
    IndexMapperBuilder axisZ();
    void checkResizability(int sizeX, int sizeY, int sizeZ);
}
