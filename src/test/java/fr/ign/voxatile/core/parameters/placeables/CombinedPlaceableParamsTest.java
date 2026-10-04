package fr.ign.voxatile.core.parameters.placeables;

import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.placeables.CombinedPlaceable;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.placeables.TestingPlaceable;
import fr.ign.voxatile.core.utils.random.TestingSeed;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;
import fr.ign.voxatile.core.world.TestingVoxelTile;

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
