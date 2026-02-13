package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.SizedIterable;

public class CompositeIndexMapper implements IndexMapper {
    IndexMapper[] mappers;

    public CompositeIndexMapper(IndexMapper[] mappers) {
        this.mappers = mappers;
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public PlaceableIndex placeable(int coordinateValue, int index) {
        return mappers[index].placeable(coordinateValue);
    }

    @Override
    public SizedIterable<StructureIndex> structure() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
