package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

public interface IndexMapper {
    PlaceableIndex placeable(int coordinateValue);
    SizedIterable<StructureIndex> structure();

    /**
     * {Returns the total size of this index axis mapper.}
     */
    int size();

    record PlaceableIndex(int index, int coordinateValue){};
    // TODO: length -> size
    record StructureIndex(int index, int size){};
}
