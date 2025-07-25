package com.ignfab.minalac.generator.parameters.placeables;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.placeables.CombinedPlaceable;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.TestingPlaceable;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class CombinedPlaceableParamsTest {
    @Test
    public void testValidate() throws JsonProcessingException {
        CombinedPlaceableParams params = new CombinedPlaceableParams(List.of(), null);

        params.placeableParams.add(TestingPlaceableParams.VALID);
        params.placeableParams.add(TestingPlaceableParams.VALID);
        assertDoesNotThrow(params::validate);

        params.placeableParams.add(TestingPlaceableParams.INVALID);
        params.placeableParams.add(TestingPlaceableParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        TestingPlaceable placeable = new TestingPlaceable();
        CombinedPlaceableParams params = new CombinedPlaceableParams(List.of(), null);
        params.placeableParams.add(new TestingPlaceableParams(placeable));
        params.placeableParams.add(new TestingPlaceableParams(placeable));
        params.placeableParams.add(new TestingPlaceableParams(placeable));

        Placeable result = assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
        CombinedPlaceable combined = assertInstanceOf(CombinedPlaceable.class, result);
        combined.place(new TestingVoxelTile(WorldBBox3d.ORIGIN), 0, 0, 0);
        assertEquals(3, placeable.timesPlaced());
    }
}
