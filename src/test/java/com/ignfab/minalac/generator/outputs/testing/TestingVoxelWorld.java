package com.ignfab.minalac.generator.outputs.testing;

import java.io.File;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing purpose voxel world output.
 *
 * TestingVoxelWorld does not produce any file output. It is intended to be used in unit tests
 * as output world.
 */
public class TestingVoxelWorld extends VoxelWorld {
    private WorldBBox3d maxLimits;
    private String[] voxels;

    /**
     * Constructs a new TestingVoxelWorld.
     *
     * @param bbox Bounding box of the voxel world to create
     *
     * Beware: Avoid large bounding boxes!
     * Each voxel is stored in memory as a string, which could be very large.
     */
    public TestingVoxelWorld(WorldBBox3d bbox) {
        super(null);
        maxLimits = bbox;
        voxels = new String[bbox.size().volume()];
        if (bbox != WorldBBox3d.EMPTY)
            super.setLimits(bbox);
    }

    @Override
    public WorldBBox3d maxLimits() {
        return maxLimits;
    }

    // Avoid "Limits already set" when performing deserialization tests
    // and get rid of .setLimits in rendering tests
    @Override
    public void setLimits(WorldBBox3d bbox) {
    }

    @Override
    public void save(File destination) throws MapWriteException {}

    // This method should not be called with out of bounds coordinate
    private int index(int x, int y, int z) {
        return x - limits().minX() + limits().sizeX()
            * (y - limits().minY() + limits().sizeY()
            * (z - limits().minZ()));
    }

    protected void set(int x, int y, int z, TestingVoxelType voxelType) {
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
    public void set(int x, int y, int z, String value) {
        if (limits().contains(x, y, z))
            voxels[index(x, y, z)] = value;
    }

    /**
     * Get voxel string value at a given position.
     *
     * @param pos position to get
     * @return voxel value at this position
     */
    public String get(WorldCoords3d pos) {
        return get(pos.x(), pos.y(), pos.z());
    }

    /**
     * Get voxel string value at a given position.
     *
     * @param x X-coordinates of position to get
     * @param y Y-coordinates of position to get
     * @param z Z-coordinates of position to get
     * @return Voxel value at this position
     */
    public String get(int x, int y, int z) {
        if (!limits().contains(x, y, z))
            throw new IndexOutOfBoundsException("Specified coordinates (%d, %d, %d) are off limit".formatted(x, y, z));
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
    public void assertVoxel(String expected, int x, int y, int z, String message) {
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
    public void assertVoxel(String expected, int x, int y, int z) {
        assertVoxel(expected, x, y, z, "Voxel mismatch at (%d, %d, %d)".formatted(x, y, z));
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param pos Position of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxel(String expected, WorldCoords3d pos, String message) {
        assertVoxel(expected, pos.x(), pos.y(), pos.z(), message);
    }

    /**
     * Asserts a voxel has an expected value.
     *
     * @param expected Expected value
     * @param pos Position of tested voxel
     */
    public void assertVoxel(String expected, WorldCoords3d pos) {
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
    public void assertVoxelNull(int x, int y, int z, String message) {
        assertNull(get(x, y, z), message);
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     */
    public void assertVoxelNull(int x, int y, int z) {
        assertVoxelNull(x, y, z, "Not null voxel at (%d, %d, %d)".formatted(x, y, z));
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param pos Position of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxelNull(WorldCoords3d pos, String message) {
        assertVoxelNull(pos.x(), pos.y(), pos.z(), message);
    }

    /**
     * Asserts a voxel has no value.
     *
     * @param pos Position of tested voxel
     */
    public void assertVoxelNull(WorldCoords3d pos) {
        assertVoxelNull(pos.x(), pos.y(), pos.z());
    }

    /**
     * Asserts a voxel has a value.
     *
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxelNotNull(int x, int y, int z, String message) {
        assertNotNull(get(x, y, z), message);
    }

    /**
     * Asserts a voxel has a value.
     *
     * @param x x-coordinate of tested voxel
     * @param y y-coordinate of tested voxel
     * @param z z-coordinate of tested voxel
     */
    public void assertVoxelNotNull(int x, int y, int z) {
        assertVoxelNotNull(x, y, z, "Null voxel at (%d, %d, %d)".formatted(x, y, z));
    }

    /**
     * Asserts a voxel has a value.
     *
     * @param pos Position of tested voxel
     * @param message Message to be displayed in case of failure
     */
    public void assertVoxelNotNull(WorldCoords3d pos, String message) {
        assertVoxelNotNull(pos.x(), pos.y(), pos.z(), message);
    }

    /**
     * Asserts a voxel has a value.
     *
     * @param pos Position of tested voxel
     */
    public void assertVoxelNotNull(WorldCoords3d pos) {
        assertVoxelNotNull(pos.x(), pos.y(), pos.z());
    }
}
