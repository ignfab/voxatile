package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import java.util.Collection;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;

public class IdentityIndexMapper implements IndexMapper {
    int length;

    public IdentityIndexMapper(int length) {
        this.length = length;
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        return new IndexMapper.PlaceableIndex(0, coordinateValue);
    }

    @Override
    public Collection<StructureIndex> structures() {
        return IntStream.range(0,1).mapToObj(i -> new StructureIndex(0,length)).toList();
    }

    @Override
    public int size() {
        return length;
    }
}
