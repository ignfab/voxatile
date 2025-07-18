package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class ElasticPlaceableStructureParamsTest {
    @Test
    public void testValidate() {
        assertDoesNotThrow((new ElasticPlaceableStructureParams(TestingStructure.VALID))::validate);
        assertThrows(IllegalArgumentException.class, (new ElasticPlaceableStructureParams(TestingStructure.INVALID))::validate);
    }

    @Test
    public void testDeserialize() {
        // Maximal test
        ElasticPlaceableStructureParams maxParams = assertDoesNotThrow(() -> ParamsTester.deserialize(
            ElasticPlaceableStructureParams.class,
            """
            structure:
              - put: some-voxel
                at: [5, 2, 3]
              - put: some-voxel
                at: [0, 0, 0]
            elasticAtX: 1
            elasticAtY: 2
            elasticAtZ: 3
            """
        ));
        assertInstanceOf(PlaceableStructureParams.class, maxParams.structure);
        assertEquals(2, maxParams.elasticAtY);

        // Minimal test
        ElasticPlaceableStructureParams minParams = assertDoesNotThrow(() -> ParamsTester.deserialize(
            ElasticPlaceableStructureParams.class,
            """
            structure:
              - put: some-voxel
                at: [5, 2, 3]
              - put: some-voxel
                at: [0, 0, 0]
            """
        ));
        assertInstanceOf(PlaceableStructureParams.class, minParams.structure);
        assertNull(minParams.elasticAtX);
    }

    // TODO-PR: Might be removed. (Depends if ElasticPlaceableStructureParams is kept)
    /**
     * Class for testing placeable structure params.
     */
    public static final class TestingStructure extends PlaceableStructureParams {
        /**
         * An invalid testing placeable structure params.
         */
        public static final TestingStructure INVALID = new TestingStructure(null);
        /**
         * A valid testing placeable structure params.
         */
        public static final TestingStructure VALID = new TestingStructure(new HashMap<>());
        private final Map<WorldCoords3d, Placeable> placeable;

        private TestingStructure(Map<WorldCoords3d, Placeable> placeable) {
            this.placeable = placeable;
        }

        @Override
        public void validate() {
            if (placeable == null)
                throw new IllegalArgumentException();
        }
    }
}
