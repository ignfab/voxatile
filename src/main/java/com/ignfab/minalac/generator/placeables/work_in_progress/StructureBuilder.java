package com.ignfab.minalac.generator.placeables.work_in_progress;

public interface StructureBuilder {
    Structure build(int sizeX, int sizeY, int sizeZ);
    IndexMapperBuilder axisX();
    IndexMapperBuilder axisY();
    IndexMapperBuilder axisZ();
}
