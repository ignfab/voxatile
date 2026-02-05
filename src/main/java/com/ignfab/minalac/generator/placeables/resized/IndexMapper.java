package com.ignfab.minalac.generator.placeables.resized;

import java.util.Collection;

public interface IndexMapper {
    PlaceableIndex placeable(int coordinateValue);
    // TODO-1: Trouver le nom de la méthode
    // TODO-2: Interface java qui fait juste Iterable + size()/length() (Sans add() etc?) Sans avoir besoin de faire une interface
    Collection<StructureIndex> structures();

    /**
     * {Returns the total size of this index axis mapper.}
     */
    int size();

    record PlaceableIndex(int index, int coordinateValue){};
    record StructureIndex(int index, int size){};
}
