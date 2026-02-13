package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.SizedIterable;

public class LengthIndexMapper implements IndexMapper {
    int[] breakpointPositions;
    int[] lengths;

    public LengthIndexMapper(int... lengths) {
        // TODO: ignorer les zeros?
        // Arrays.stream(lengths).filter( l -> l >=0).toArray();
        this.lengths = lengths;
        breakpointPositions = new int[lengths.length];
        breakpointPositions[0] = lengths[0];
        for (int i = 1; i < lengths.length; i++)
            breakpointPositions[i] = breakpointPositions[i - 1] + lengths[i];
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        int a = 0;
        for (int i = 0; i < breakpointPositions.length; i++) {
            if (coordinateValue < breakpointPositions[i]) {
                return new PlaceableIndex(i, coordinateValue - a);
            }
            a = breakpointPositions[i];
        }
        throw new IndexOutOfBoundsException(String.format("%d is greater than %d", coordinateValue,size()));
    }

    @Override
    public SizedIterable<StructureIndex> structure() {
        return new SizedIterable<>() {
            @Override
            public int length() {
                return lengths.length;
            }

            @Override
            public Iterator<StructureIndex> iterator() {
                return IntStream.range(0, lengths.length)
                    .mapToObj(i -> new StructureIndex(i, lengths[i]))
                    .toList().iterator();
            }

            @Override
            public String toString() {
                return Arrays.toString(lengths);
            }
        };


    }

    @Override
    public int size() {
        return breakpointPositions[breakpointPositions.length - 1];
    }

    public static void main(String[] args) {
        // IndexMapper map = new LengthIndexMapper(new int[] {2, 5, 10});
        IndexMapper map = new LengthIndexMapper(new int[] {2, 3, 5});
        // IndexMapper map = new LengthIndexMapper(2, 3, 5);

        for (int c = 0; c < 10; c++) {
            System.out.println(c + "->" + map.placeable(c));
        }
        for (StructureIndex s : map.structure())
            System.out.println(s);
    }
}
