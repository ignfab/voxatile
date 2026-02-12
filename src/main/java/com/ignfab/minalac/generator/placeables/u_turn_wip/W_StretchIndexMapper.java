package com.ignfab.minalac.generator.placeables.u_turn_wip;

import java.util.Iterator;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.SizedIterable;

public class W_StretchIndexMapper implements IndexMapper {
    int stretchableCoord;
    int lengthAtRest;
    int length;

    // lengthAtRest is the "original size"
    // 3 cas : taille demande = taille de la struct;
    // taille demandé = taille struct - 1 (la colonne disparait)
    // taille demandé > taille struct (la colonne est repete)
    public W_StretchIndexMapper(int stretchableCoord, int lengthAtRest, int length) {
        this.stretchableCoord = stretchableCoord;
        this.lengthAtRest = lengthAtRest;
        this.length = length;
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        int r = length - lengthAtRest;
        if (coordinateValue < stretchableCoord) {
            return new PlaceableIndex(0, coordinateValue);
        } else if (coordinateValue <= stretchableCoord + r) {
            return new PlaceableIndex(0, stretchableCoord);
        } else {
            return new PlaceableIndex(0, coordinateValue - r);
        }
    }

    @Override
    public SizedIterable<StructureIndex> structure() {
        return new SizedIterable<>() {
            @Override
            public int size() {
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
