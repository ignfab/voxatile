package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.Test;

public class SimpleVoxelPatternTest {

    @Test
    public void testPlace() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(-1, -2, -3, 3, 3, 3));

        VoxelType grass = world.getFactory().createVoxelType(SemanticType.GRASS);
        VoxelType dirt = world.getFactory().createVoxelType(SemanticType.DIRT);
        VoxelType stone = world.getFactory().createVoxelType(SemanticType.STONE);
        VoxelType cobble = world.getFactory().createVoxelType(SemanticType.COBBLE);
        VoxelType brick = world.getFactory().createVoxelType(SemanticType.BRICK);

        // World voxel configuration
        /* z :    -2   -1
                +---+ +---+
        y       |bbb| |  b|
        ^       |bbb| |   |
        |       |bbb| |  b|
        + - > x +---+ +---+ */
        for (int x = -1; x <= 1; x++)
            for (int y = -2; y <= 0; y++)
                brick.place(x, y, -2); // layer of brick at z = -2
        brick.place(1, 0, -1); // |  b| at z = -1 & y = 0
        brick.place(1, -2, -1); // |  b| at z = -1 & y = -2

        // Pattern voxel configuration
        /* z:     -1    0     1
                +---+ +---+ +---+
        y       |ggg| | s | |g  |
        ^       |g g| |cds| |   |
        |       |  g| | c | |   |
        + - > x +---+ +---+ +---+ */
        SimpleVoxelPattern pattern = new SimpleVoxelPattern();

        // z = -1 : fill with grass
        pattern.set(new WorldBBox3d(-1, -1, -1, 3, 3, 1), grass);
        // z = -1 : remove the voxel at the middle -> |g g|
        pattern.set(0, 0, -1, null);
        // z = -1 : remove the two voxels -> |  g|
        pattern.remove(new WorldBBox3d(-1, -1, -1, 2, 1, 1));

        // z = 0 | s |
        pattern.set(0, 1, 0, stone);
        // z = 0 |cds|
        pattern.set(-1, 0, 0, cobble);
        pattern.set(0, 0, 0, dirt);
        pattern.set(1, 0, 0, stone);
        // z = 0 | c |
        pattern.set(0, -1, 0, cobble);

        // z = 1 |g  |
        pattern.set(-1, 1, 1, grass);

        // Pattern is placed at the center of the world
        pattern.place(0, -1, -2);

        // Expected outcome
        /* z:     -3   -2    -1
                +---+ +---+ +---+
        y       |ggg| |bsb| |g b|
        ^       |g g| |cds| |   |
        |       |  g| |bcb| |  b|
        + - > x +---+ +---+ +---+ */

        // z = -3
        // y = 0 : |ggg|
        world.assertVoxel("grass", -1, 0, -3);
        world.assertVoxel("grass", 0, 0, -3);
        world.assertVoxel("grass", 1, 0, -3);

        // y = -1 : |g g|
        world.assertVoxel("grass", -1, -1, -3);
        world.assertVoxelNull(0, -1, -3);
        world.assertVoxel("grass", 1, -1, -3);

        // y = -2 : |  g|
        world.assertVoxelNull(-1, -2, -3);
        world.assertVoxelNull(0, -2, -3);
        world.assertVoxel("grass", 1, -2, -3);

        // z = -2
        // y = 0 : |bsb|
        world.assertVoxel("brick", -1, 0, -2);
        world.assertVoxel("stone", 0, 0, -2);
        world.assertVoxel("brick", 1, 0, -2);

        // y = -1 : |cds|
        world.assertVoxel("cobble", -1, -1, -2);
        world.assertVoxel("dirt", 0, -1, -2);
        world.assertVoxel("stone", 1, -1, -2);

        // y = -2 : |bcb|
        world.assertVoxel("brick", -1, -2, -2);
        world.assertVoxel("cobble", 0, -2, -2);
        world.assertVoxel("brick", 1, -2, -2);

        // z = -1
        // y = 0 : |g b|
        world.assertVoxel("grass", -1, 0, -1);
        world.assertVoxelNull(0, 0, -1);
        world.assertVoxel("brick", 1, 0, -1);

        // y = -1 : |   |
        world.assertVoxelNull(-1, -1, -1);
        world.assertVoxelNull(0, -1, -1);
        world.assertVoxelNull(1, -1, -1);

        // y = -2 : |  b|
        world.assertVoxelNull(-1, -2, -1);
        world.assertVoxelNull(0, -2, -1);
        world.assertVoxel("brick", 1, -2, -1);
    }

    @Test
    public void testPlaceWithEmptyPattern() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(3, 4, 5, 2, 1, 1));

        VoxelType grass = world.getFactory().createVoxelType(SemanticType.GRASS);
        grass.place(4, 4, 5);

        SimpleVoxelPattern emptyPattern = new SimpleVoxelPattern();
        emptyPattern.place(3, 4, 5);
        emptyPattern.place(4, 4, 5);

        world.assertVoxelNull(3, 4, 5);
        world.assertVoxel("grass", 4, 4, 5);
    }
}
