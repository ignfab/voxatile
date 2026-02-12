package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import java.util.Iterator;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.SizedIterable;

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
    public SizedIterable<StructureIndex> structure() {
        return new SizedIterable<>() {
            @Override
            public int length() {
                return 1;
            }

            @Override
            public Iterator<StructureIndex> iterator() {
                return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, length)).toList().iterator();
            }
        };
    }

    @Override
    public int size() {
        return length;
    }
}
