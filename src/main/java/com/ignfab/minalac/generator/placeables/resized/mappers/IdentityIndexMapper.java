package com.ignfab.minalac.generator.placeables.resized.mappers;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;

public class IdentityIndexMapper implements IndexMapper {
    private final int length;

    public IdentityIndexMapper(int length) {
        if (length < 0)
            throw new IllegalArgumentException("length can not be negative");
        this.length = length;
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        // TODO-3 : Faut t-il mettre un offset ou considerer que tous les IndexMapper commencent à zéro?
        if (0 > coordinateValue || coordinateValue >= length)
            throw new IndexOutOfBoundsException("Provided position is out of bounds");
        return new IndexMapper.PlaceableIndex(0, coordinateValue);
    }

    @Override
    public Collection<StructureIndex> structures() {
        if (length == 0) return Collections.emptyList();
        return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, length)).toList();
    }

    @Override
    public int size() {
        return length;
    }
}
