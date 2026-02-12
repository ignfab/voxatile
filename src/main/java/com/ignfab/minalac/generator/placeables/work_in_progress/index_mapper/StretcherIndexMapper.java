package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import java.util.Iterator;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.SizedIterable;

public class StretcherIndexMapper implements IndexMapper {
    int stretchableCoordinate;
    int lengthAtRest;
    int length;

    // lengthAtRest is the "original size"
    // 3 cas : taille demande = taille de la struct;
    // taille demandé = taille struct - 1 (la colonne disparait)
    // taille demandé > taille struct (la colonne est repete)
    public StretcherIndexMapper(int stretchableCoordinate, int lengthAtRest, int length) {
        this.stretchableCoordinate = stretchableCoordinate;
        this.lengthAtRest = lengthAtRest;
        this.length = length;
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        int r = length - lengthAtRest;
        if (coordinateValue < stretchableCoordinate) {
            return new PlaceableIndex(0, coordinateValue);
        } else if (coordinateValue <= stretchableCoordinate + r) {
            return new PlaceableIndex(0, stretchableCoordinate);
        } else {
            return new PlaceableIndex(0, coordinateValue - r);
        }
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
                return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, lengthAtRest)).toList().iterator();
            }
        };
    }

    @Override
    public int size() {
        return length;
    }
}
