package com.ignfab.minalac.generator.placeables.resized.mappers;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;

public class StretcherIndexMapper implements IndexMapper {
    int stretchableCoordinate;
    int lengthAtRest;
    int length;

    // lengthAtRest is the "original size"
    // 3 cas : taille demande = taille de la struct;
    // taille demandé = taille struct - 1 (la colonne disparait)
    // taille demandé > taille struct (la colonne est repete)
    public StretcherIndexMapper(int stretchableCoordinate, int lengthAtRest, int length) {
        if (length < 0 || lengthAtRest < 0)
            throw new IllegalArgumentException("length can not be negative");
        if (length - lengthAtRest < - 1)
            throw new IllegalArgumentException("Can not be squeezed more than 1");
        // TODO-6: stretchable coord doit être [0, lengthAtRest - 1] => Faire offset?
        // Si ce test => Impossible de faire des structures vides (if (lengthAtRest == 0) return Collections.emptyList();)
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
    public Collection<StructureIndex> structures() {
        if (lengthAtRest == 0) return Collections.emptyList();
        return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, lengthAtRest)).toList();
        /*
        return new SizedIterable<>() {
            @Override
            public int length() {
                return 1;
            }

            @Override
            public Iterator<StructureIndex> iterator() {
                return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, lengthAtRest)).toList().iterator();
            }
        };*/
    }

    @Override
    public int size() {
        if (lengthAtRest == 0) return 0;
        return length;
    }
}
