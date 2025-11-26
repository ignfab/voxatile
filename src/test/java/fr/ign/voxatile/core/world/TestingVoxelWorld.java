package fr.ign.voxatile.core.world;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import fr.ign.voxatile.core.utils.world2d.WorldBBox2d;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;

/**
 * Testing purpose {@link VoxelWorld} intended to be used in unit tests.
 */
public class TestingVoxelWorld extends VoxelWorld {
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-100, -100, -100),
        new WorldCoords3d(100, 100, 100)
    );

    /**
     * A default {@code TestingVoxelWorld} instance to pass as argument when it won't actually be used.
     */
    public static final TestingVoxelWorld UNUSED = new TestingVoxelWorld();

    /**
     * Creates a new TestingVoxelWorld.
     */
    public TestingVoxelWorld() {
        super(new VoxelWorldMetadata(), null);
    }

    /**
     * Creates a new {@code TestingVoxelWorld}.
     *
     * @param destination destination folder for the world
     */
    public TestingVoxelWorld(File destination) {
        super(new VoxelWorldMetadata(), destination);
    }

    @Override
    public void initialize() throws MapWriteException {}

    @Override
    public void finalizeAndSave() {}

    /**
     * {@inheritDoc}
     *
     * Beware: Avoid large limits!
     * Each voxel is stored in memory as a string, which could be very large.
     */
    @Override
    public VoxelTile newTile(WorldBBox3d limits) {
        return new TestingVoxelTile(limits);
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        return Collections.singleton(maxLimits().to2d());
    }
}
