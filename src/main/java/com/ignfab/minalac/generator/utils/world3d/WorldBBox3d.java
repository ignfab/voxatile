package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.iterator.WorldBBox3dIterator;

/**
 * The {@code WorldBBox3d} class represents a three-dimensional bounding box that surrounds an area of the voxel world.
 * This BBOX is defined by a minimum point and a maximum point. These points are included in the bounding box.
 * An explanation of the voxel world coordinates system can be found on {@link WorldCoords3d}.
 *
 * @see WorldCoords3d
 */
public class WorldBBox3d implements Iterable<WorldCoords3d> {
    private final WorldCoords3d min, max;
    private final WorldSize3d size;

    /**
     * Constructs a new {@link WorldBBox3d} by providing a starting position and the desired size of the bounding box.
     *
     * @param origin the starting position's coordinates (minimum point).
     * @param size   the bounding box size.
     */
    public WorldBBox3d(WorldCoords3d origin, WorldSize3d size) {
        this.size = size;
        min = origin;
        max = new WorldCoords3d(
                min.x() + size.x() - 1,
                min.y() + size.y() - 1,
                min.z() + size.z() - 1
        );
    }

    /**
     * Constructs a new {@link WorldBBox3d} by providing a starting position and the desired size of the bounding box.
     *
     * @param originX the x-coordinate of the starting position.
     * @param originY the y-coordinate of the starting position.
     * @param originZ the z-coordinate of the starting position.
     * @param sizeX   the size along the x-axis of the bounding box.
     * @param sizeY   the size along the y-axis of the bounding box.
     * @param sizeZ   the size along the z-axis of the bounding box.
     */
    public WorldBBox3d(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ) {
        this(new WorldCoords3d(originX, originY, originZ), new WorldSize3d(sizeX, sizeY, sizeZ));
    }

    /**
     * Constructs a new {@link WorldBBox3d} by providing the minimum and maximum points of the bounding box.
     *
     * @param min the minimum point's coordinates.
     * @param max the maximum point's coordinates.
     * @throws IllegalArgumentException if any coordinates of the maximum point is less than its counterpart in the minimum point.
     */
    public WorldBBox3d(WorldCoords3d min, WorldCoords3d max) {
        if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z())
            throw new IllegalArgumentException("Minimum point must be less than or equal to maximum point");
        this.min = min;
        this.max = max;
        size = new WorldSize3d(max.x() - min.x() + 1, max.y() - min.y() + 1, max.z() - min.z() + 1);
    }

    /**
     * Create a new {@link WorldBBox3d} from an existing {@link WorldBBox2d} and additional components along the z-axis.
     *
     * @param bbox an existing {@link WorldBBox2d} object
     * @param originZ the z-coordinate of the starting position
     * @param sizeZ the size along the z-axis
     */
    public WorldBBox3d(WorldBBox2d bbox, int originZ, int sizeZ) {
        this(bbox.getMin().to3d(originZ), bbox.getSize().to3d(sizeZ));
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @param z the z-coordinate value.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(int x, int y, int z) {
        return min.x() <= x && x <= max.x() && min.y() <= y && y <= max.y() && min.z() <= z && z <= max.z();
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param coords the coordinates to be checked.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(WorldCoords3d coords) {
        return contains(coords.x(), coords.y(), coords.z());
    }

    /**
     * Returns the size of the bounding box.
     *
     * @return the size of the bounding box as a {@link WorldSize3d}.
     */
    public WorldSize3d getSize() {
        return size;
    }

    /**
     * Returns the bounding box size along the x-axis.
     *
     * @return the bounding box size along the x-axis.
     */
    public int getSizeX() {
        return size.x();
    }

    /**
     * Returns the bounding box size along the y-axis.
     *
     * @return the bounding box size along the y-axis.
     */
    public int getSizeY() {
        return size.y();
    }

    /**
     * Returns the bounding box size along the z-axis.
     *
     * @return the bounding box size along the z-axis.
     */
    public int getSizeZ() {
        return size.z();
    }

    /**
     * Returns the minimum point.
     *
     * @return the {@link WorldCoords3d} of the minimum point.
     */
    public WorldCoords3d getMin() {
        return min;
    }

    /**
     * Returns the minimum point x-coordinate.
     *
     * @return the x-coordinate of the minimum point.
     */
    public int getMinX() {
        return min.x();
    }

    /**
     * Returns the minimum point y-coordinate.
     *
     * @return the y-coordinate of the minimum point.
     */
    public int getMinY() {
        return min.y();
    }

    /**
     * Returns the minimum point z-coordinate.
     *
     * @return the z-coordinate of the minimum point.
     */
    public int getMinZ() {
        return min.z();
    }

    /**
     * Returns the maximum point.
     *
     * @return the {@link WorldCoords3d} of the maximum point.
     */
    public WorldCoords3d getMax() {
        return max;
    }

    /**
     * Returns the maximum point x-coordinate.
     *
     * @return the x-coordinate of the maximum point.
     */
    public int getMaxX() {
        return max.x();
    }

    /**
     * Returns the maximum point y-coordinate.
     *
     * @return the y-coordinate of the maximum point.
     */
    public int getMaxY() {
        return max.y();
    }

    /**
     * Returns the maximum point z-coordinate.
     *
     * @return the z-coordinate of the maximum point.
     */
    public int getMaxZ() {
        return max.z();
    }

    /**
     * Returns an iterator.
     *
     * @return a {@link WorldBBox3dIterator} to iterate over all the points contained in the bounding box.
     */
    @Override
    public WorldBBox3dIterator iterator() {
        return new WorldBBox3dIterator(this);
    }

    /**
     * Convert this {@link WorldBBox3d} to {@link WorldBBox2d}, dropping its components along the z-axis.
     * The region represented by this bbox will be flattened.
     *
     * @return a new {@link WorldBBox2d} with the current components along the x- and y-axes
     */
    public WorldBBox2d to2d() {
        return new WorldBBox2d(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldBBox3d that = (WorldBBox3d) o;
        return min.equals(that.min) && max.equals(that.max);
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorldBBox3d{" +
            "min=" + min +
            ", max=" + max +
            ", size=" + size +
            '}';
    }
}
