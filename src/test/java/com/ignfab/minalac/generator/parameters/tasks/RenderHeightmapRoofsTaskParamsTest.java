package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;
import com.ignfab.minalac.generator.placeables.TestingPlaceable;
import com.ignfab.minalac.generator.tasks.RenderHeightmapRoofsTask;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class RenderHeightmapRoofsTaskParamsTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RenderHeightmapRoofsTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new TestingPlaceableParams(new TestingPlaceable())
        ));
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new RenderHeightmapRoofsTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new TestingPlaceableParams(new TestingPlaceable())
        )::validate);
    }

    @Test
    public void testCreate() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("thisIsEmpty", 5));

        assertInstanceOf(
            RenderHeightmapRoofsTask.class,
            new RenderHeightmapRoofsTaskParams(
                TestingModelSelectionParams.VALID,
                TestingHeightmapParams.VALID,
                new TestingPlaceableParams(new TestingPlaceable())
            ).create(generation)
        );
    }
}
