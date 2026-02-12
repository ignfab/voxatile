package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public interface ResizedStructureBuilder {
    Structure build(int sizeX, int sizeY, int sizeZ);
    IndexMapperBuilder axisX();
    IndexMapperBuilder axisY();
    IndexMapperBuilder axisZ();
    void checkResizability(int sizeX, int sizeY, int sizeZ);
}
