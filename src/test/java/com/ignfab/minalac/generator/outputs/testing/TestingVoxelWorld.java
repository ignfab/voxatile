package com.ignfab.minalac.generator.outputs.testing;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Testing purpose voxel world output.
 *
 * TestingVoxelWorld does not produce any file output. It is intended to be used in unit tests
 * as output world.
 */
public class TestingVoxelWorld implements VoxelWorld {
    private VoxelTypeFactory factory;
    private WorldBBox3d bbox;
    private String[] voxels;
    private VoxelWorldMetadata metadata;

    /**
     * Constructs a new TestingVoxelWorld.
     *
     * @param bbox Bounding box of the voxel world to create
     *
     * Beware: Avoid large bounding boxes!
     * Each voxel is stored in memory as a string, which could be very large.
     */
    public TestingVoxelWorld(WorldBBox3d bbox) {
        this.bbox = bbox;
        factory = new TestingVoxelTypeFactory(this);
        voxels = new String[bbox.getSize().volume()];
        metadata = new VoxelWorldMetadata();
    }

    @Override
    public VoxelTypeFactory getFactory() {
        return this.factory;
    }

    @Override
    public void save(File destination) throws MapWriteException {}

    @Override
    public VoxelWorldMetadata getMetadata() {
        return metadata;
    };

    private int index(int x, int y, int z) throws OutOfWorldException {
        if (!bbox.contains(x, y, z))
            throw new OutOfWorldException();
        return x - bbox.getMinX() + bbox.getSizeX()
            * (y - bbox.getMinY() + bbox.getSizeY()
            * (z - bbox.getMinZ()));
    }

    protected void set(int x, int y, int z, TestingVoxelType voxelType) throws OutOfWorldException {
        set(x, y, z, voxelType.getType());
    }

    /**
     * Set voxel string value at a given position.
     *
     * @param x X-coordinates of position to set
     * @param y Y-coordinates of position to set
     * @param z Z-coordinates of position to set
     * @param value Voxel value to set at this position
     */
    public void set(int x, int y, int z, String value) throws OutOfWorldException {
        voxels[index(x, y, z)] = value;
    }

    /**
     * Get voxel string value at a given position.
     *
     * @param x X-coordinates of position to get
     * @param y Y-coordinates of position to get
     * @param z Z-coordinates of position to get
     * @return Voxel value at this position
     */
    public String get(int x, int y, int z) throws OutOfWorldException {
        return voxels[index(x, y, z)];
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxel(String expected, int x, int y, int z, String message) throws OutOfWorldException {
        assertEquals(expected, get(x, y, z), message);
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     */
    public void assertVoxel(String expected, int x, int y, int z) throws OutOfWorldException {
        assertVoxel(expected, x, y, z, "Voxel mismatch at (%d, %d, %d)".formatted(x, y, z));
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param pos Position of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxel(String expected, WorldCoords3d pos, String message) throws OutOfWorldException {
        assertVoxel(expected, pos.x(), pos.y(), pos.z(), message);
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param pos Position of tested voxel
     */
    public void assertVoxel(String expected, WorldCoords3d pos) throws OutOfWorldException {
        assertVoxel(expected, pos.x(), pos.y(), pos.z());
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxelNull(int x, int y, int z, String message) throws OutOfWorldException {
        assertNull(get(x, y, z), message);
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     */
    public void assertVoxelNull(int x, int y, int z) throws OutOfWorldException {
        assertVoxelNull(x, y, z, "Voxel mismatch at (%d, %d, %d)".formatted(x, y, z));
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param pos Position of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxelNull(WorldCoords3d pos, String message) throws OutOfWorldException {
        assertVoxelNull(pos.x(), pos.y(), pos.z(), message);
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param pos Position of tested voxel
     */
    public void assertVoxelNull(WorldCoords3d pos) throws OutOfWorldException {
        assertVoxelNull(pos.x(), pos.y(), pos.z());
    }
}
