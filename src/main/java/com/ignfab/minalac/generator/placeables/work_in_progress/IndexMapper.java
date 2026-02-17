package com.ignfab.minalac.generator.placeables.work_in_progress;

import java.util.Collection;

public interface IndexMapper {
    PlaceableIndex placeable(int coordinateValue);
    Collection<StructureIndex> structures();

    /**
     * {Returns the total size of this index axis mapper.}
     */
    int size();

    record PlaceableIndex(int index, int coordinateValue){};
    record StructureIndex(int index, int size){};
}
