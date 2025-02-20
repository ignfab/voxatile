package com.ignfab.minalac.generator.parameters.placeables.structures;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.TestingVoxelTypeParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.Placeable;

import static org.junit.jupiter.api.Assertions.*;

public class StackStructureParamsTest {

    @Test
    void testDeserializeEmptyStack() {
        StackStructureParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(StackStructureParams.class,
            """
                type: stack
                layers:
            """));

        assertEquals(StackStructureParams.Direction.UPWARDS, params.direction);
        assertNull(params.layers);
        assertDoesNotThrow(() -> params.create(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1))));
    }

    @Test
    void testDeserializeDownwardsStack() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(-1, -1, -4, 2, 2, 6));

        StackStructureParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(StackStructureParams.class,
            """
                type: stack
                direction: downwards
                layers:
                  - material: a
                  - material: b
                    height: 3
            """));

        assertEquals(StackStructureParams.Direction.DOWNWARDS, params.direction);
        assertEquals(2, params.layers.size());
        assertEquals(1, params.layers.get(0).height);
        assertEquals("a", assertInstanceOf(TestingVoxelTypeParams.class, params.layers.get(0).material).name);
        assertEquals(3, params.layers.get(1).height);
        assertEquals("b", assertInstanceOf(TestingVoxelTypeParams.class, params.layers.get(1).material).name);

        Placeable placeable = assertDoesNotThrow(() -> params.create(world));

        // We need to test deserialization result by placing structure
        // (we are not supposed to know how placeable is managed)
        placeable.place(0, 0, 0);
        world.assertVoxelNull(0, 0, 1);
        world.assertVoxel("a", 0, 0, 0);
        world.assertVoxel("b", 0, 0, -1);
        world.assertVoxel("b", 0, 0, -2);
        world.assertVoxel("b", 0, 0, -3);
        world.assertVoxelNull(0, 0, -4);
        for (WorldCoords3d pos : world.limits())
            if (pos.x() != 0 && pos.y() != 0)
                world.assertVoxelNull(pos, "Voxel should be null at %s".formatted(pos));
    }

    @Test
    void testDeserializeUpwardsStack() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(-1, -1, -1, 2, 2, 6));

        StackStructureParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(StackStructureParams.class,
            """
                type: stack
                direction: upwards
                layers:
                  - material: c
                    height: 1
                  - material: b
                    height: 2
                  - material: a
            """));

        assertEquals(StackStructureParams.Direction.UPWARDS, params.direction);
        assertEquals(3, params.layers.size());
        assertEquals(1, params.layers.get(0).height);
        assertEquals("c", assertInstanceOf(TestingVoxelTypeParams.class, params.layers.get(0).material).name);
        assertEquals(2, params.layers.get(1).height);
        assertEquals("b", assertInstanceOf(TestingVoxelTypeParams.class, params.layers.get(1).material).name);
        assertEquals(1, params.layers.get(2).height);
        assertEquals("a", assertInstanceOf(TestingVoxelTypeParams.class, params.layers.get(2).material).name);

        Placeable placeable = assertDoesNotThrow(() -> params.create(world));

        // We need to test deserialization result by placing structure
        // (we are not supposed to know how placeable is managed)
        placeable.place(0, 0, 0);
        world.assertVoxelNull(0, 0, -1);
        world.assertVoxel("c", 0, 0, 0);
        world.assertVoxel("b", 0, 0, 1);
        world.assertVoxel("b", 0, 0, 2);
        world.assertVoxel("a", 0, 0, 3);
        world.assertVoxelNull(0, 0, 4);
        for (WorldCoords3d pos : world.limits())
            if (pos.x() != 0 && pos.y() != 0)
                world.assertVoxelNull(pos, "Voxel should be null at %s".formatted(pos));
    }
}
