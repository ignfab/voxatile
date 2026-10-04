package fr.ign.voxatile.core.parameters.heightmaps;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.generation.heightmaps.HeightmapDeclaration;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class CappedManhattanHeightmapParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new TestingGeneration();
        generation.heightmaps().add(new HeightmapDeclaration("ground", 0));

        CappedManhattanHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            CappedManhattanHeightmapParams.class,
            """
            manhattan: ground
            maximumDistance: 5
            targetValue: 4
            """
        ));
        assertInstanceOf(WritableHeightmapParams.class, params.manhattan);
        assertEquals(5, params.maximumDistance);
        assertEquals(4, params.targetValue);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation.heightmaps()));

        assertThrows(JacksonException.class,
            () -> ParamsTester.deserialize(
                CappedManhattanHeightmapParams.class,
                """
                manhattan:
                maximumDistance: 5
                targetValue: 4
                """
        ));
    }

    @Test
    public void testValidate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new CappedManhattanHeightmapParams(
                    TestingHeightmapParams.INVALID
                ).validate()
        );

        CappedManhattanHeightmapParams paramsWithDistanceInvalid = new CappedManhattanHeightmapParams(
            new CappedManhattanHeightmapParams(TestingHeightmapParams.VALID)
        );

        paramsWithDistanceInvalid.maximumDistance = -1;
        assertThrows(IllegalArgumentException.class, paramsWithDistanceInvalid::validate);
    }

}
