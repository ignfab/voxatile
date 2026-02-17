package com.ignfab.minalac.generator.placeables.resized.mappers;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;

public class LengthIndexMapper implements IndexMapper {
    private final int[] breakpointPositions;
    private final int[] lengths;

    public LengthIndexMapper(int... lengths) {
        // TODO-4: Voir s'il faut ou pas ignorer les zeros
        // Arrays.stream(lengths).filter( l -> l >=0).toArray();
        // TODO-5 Faire en une passe et améliorer
        for (int length : lengths)
            if (length < 0)
                throw new IllegalArgumentException("length can not be negative");
        this.lengths = lengths;
        breakpointPositions = new int[lengths.length];
        if (lengths.length > 0)
            breakpointPositions[0] = lengths[0];
        for (int i = 1; i < lengths.length; i++)
            breakpointPositions[i] = breakpointPositions[i - 1] + lengths[i];
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        if (0 > coordinateValue || coordinateValue >= breakpointPositions[breakpointPositions.length - 1])
            throw new IndexOutOfBoundsException("Provided position is out of bounds");
        int a = 0;
        for (int i = 0; i < breakpointPositions.length; i++) {
            if (coordinateValue < breakpointPositions[i]) {
                return new PlaceableIndex(i, coordinateValue - a);
            }
            a = breakpointPositions[i];
        }
        throw new IndexOutOfBoundsException(String.format("%d is greater than %d", coordinateValue, size()));
    }

    @Override
    public Collection<StructureIndex> structures() {
        if (lengths.length == 0) return Collections.emptyList();
        return IntStream.range(0, lengths.length)
            .mapToObj(i -> new StructureIndex(i, lengths[i]))
            .toList();
    }

    @Override
    public int size() {
        return breakpointPositions[breakpointPositions.length - 1];
    }
}
