package com.ignfab.minalac.generator.parameters.placeables;

import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.placeables.CombinedPlaceable;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.TestingPlaceable;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.TestingVoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class CombinedPlaceableParamsTest {
    @Test
    public void testValidate() throws JacksonException {
        PlaceableParams invalid = new TestingPlaceableParams(null);
        PlaceableParams valid = new TestingPlaceableParams(new TestingPlaceable());

        CombinedPlaceableParams params = new CombinedPlaceableParams();

        params.placeableParams = List.of(valid, valid);
        assertDoesNotThrow(params::validate);

        params.placeableParams = List.of(invalid, valid);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() throws JacksonException {
        TestingPlaceable placeable = new TestingPlaceable();
        CombinedPlaceableParams params = new CombinedPlaceableParams();
        params.placeableParams = List.of(
            new TestingPlaceableParams(placeable),
            new TestingPlaceableParams(placeable),
            new TestingPlaceableParams(placeable)
        );

        Placeable result = assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
        CombinedPlaceable combined = assertInstanceOf(CombinedPlaceable.class, result);
        combined.place(new TestingVoxelTile(WorldBBox3d.ORIGIN), 0, 0, 0);
        assertEquals(3, placeable.timesPlaced());
    }
}
