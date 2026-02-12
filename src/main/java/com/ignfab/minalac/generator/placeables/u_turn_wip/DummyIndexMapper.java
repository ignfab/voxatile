package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.SizedIterable;

public class DummyIndexMapper implements IndexMapper {

    public DummyIndexMapper() {
    }

    @Override
    public PlaceableIndex placeable(int coordinateValue) {
        throw new UnsupportedOperationException("Not implemented yet");
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
