package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world2d.iterator.WorldBBox2dIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * The {@code WorldBBox2d} class represents a two-dimensional bounding box that surrounds an area on the surface (xy-plane) of the voxel world.
 * This BBOX is defined by a minimum point and a maximum point. These points are included in the bounding box.
 * An explanation of the voxel world coordinates system can be found on {@link com.ignfab.minalac.generator.utils.world2d.WorldCoords2d}.
 *
 * @see WorldCoords2d
 */
public class WorldBBox2d implements Iterable<WorldCoords2d> {
    private final WorldCoords2d min;
    private final WorldCoords2d max;
    private final WorldSize2d size;

    /**
     * Creates a new {@code WorldBBox2d} by providing a starting position and the desired size of the bounding box.
     *
     * @param origin the starting position's coordinates (minimum point).
     * @param size   the bounding box size.
     */
    public WorldBBox2d(WorldCoords2d origin, WorldSize2d size) {
        this.size = size;
        min = origin;
        max = new WorldCoords2d(
                min.x() + size.x() - 1,
                min.y() + size.y() - 1
        );
    }

    /**
     * Creates a new {@code WorldBBox2d} by providing a starting position and the desired size of the bounding box.
     *
     * @param originX the x-coordinate of the starting position.
     * @param originY the y-coordinate of the starting position.
     * @param sizeX   the size along the x-axis of the bounding box.
     * @param sizeY   the size along the y-axis of the bounding box.
     */
    public WorldBBox2d(int originX, int originY, int sizeX, int sizeY) {
        this(new WorldCoords2d(originX, originY), new WorldSize2d(sizeX, sizeY));
    }

    /**
     * Creates a new {@link WorldBBox2d} containing all given coordinates.
     *
     * @param first a first mandatory coordinate that should be in resulting box
     * @param others a list of coordinate that should be in resulting box
     */
    public WorldBBox2d(WorldCoords2d first, WorldCoords2d... others) {
        int minX = first.x();
        int minY = first.y();
        int maxX = first.x();
        int maxY = first.y();

        for (WorldCoords2d coord : others) {
            minX = Math.min(minX, coord.x());
            minY = Math.min(minY, coord.y());
            maxX = Math.max(maxX, coord.x());
            maxY = Math.max(maxY, coord.y());
        }

        min = new WorldCoords2d(minX, minY);
        max = new WorldCoords2d(maxX, maxY);
        size = new WorldSize2d(maxX - minX + 1, maxY - minY + 1);
    }

    /**
     * Creates a new {@link WorldBBox2d} from an existing {@link WorldBBox3d}, dropping its components along the z-axis.
     * The region represented by this bbox will be flattened.
     *
     * @param bbox an existing {@link WorldBBox3d} object
     */
    public WorldBBox2d(WorldBBox3d bbox) {
        this(bbox.min().to2d(), bbox.size().to2d());
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(int x, int y) {
        return min.x() <= x && x <= max.x() && min.y() <= y && y <= max.y();
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param coords the coordinates to be checked.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(WorldCoords2d coords) {
        return contains(coords.x(), coords.y());
    }

    /**
     * Returns {@code true} if the given bbox is in this bounding box.
     *
     * @param bbox the bbox to be checked.
     * @return {@code true} if the provided bbox is in this bounding box.
     */
    public boolean contains(WorldBBox2d bbox) {
        return contains(bbox.min()) && contains(bbox.max());
    }

    /**
     * Returns the size of the bounding box.
     *
     * @return the size of the bounding box as a {@code WorldSize2d}.
     */
    public WorldSize2d size() {
        return size;
    }

    /**
     * Returns the bounding box size along the x-axis.
     *
     * @return the bounding box size along the x-axis.
     */
    public int sizeX() {
        return size.x();
    }

    /**
     * Returns the bounding box size along the y-axis.
     *
     * @return the bounding box size along the y-axis.
     */
    public int sizeY() {
        return size.y();
    }

    /**
     * Returns the minimum point.
     *
     * @return the {@code WorldCoords2d} of the minimum point.
     */
    public WorldCoords2d min() {
        return min;
    }

    /**
     * Returns the minimum point x-coordinate.
     *
     * @return the x-coordinate of the minimum point.
     */
    public int minX() {
        return min.x();
    }

    /**
     * Returns the minimum point y-coordinate.
     *
     * @return the y-coordinate of the minimum point.
     */
    public int minY() {
        return min.y();
    }

    /**
     * Returns the maximum point.
     *
     * @return the {@code WorldCoords2d} of the maximum point.
     */
    public WorldCoords2d max() {
        return max;
    }

    /**
     * Returns the maximum point x-coordinate.
     *
     * @return the x-coordinate of the maximum point.
     */
    public int maxX() {
        return max.x();
    }

    /**
     * Returns the maximum point y-coordinate.
     *
     * @return the y-coordinate of the maximum point.
     */
    public int maxY() {
        return max.y();
    }

    /**
     * Returns an iterator.
     *
     * @return a {@code WorldBBox2dIterator} to iterate over all the points contained in the bounding box.
     */
    @Override
    public WorldBBox2dIterator iterator() {
        return new WorldBBox2dIterator(this);
    }

    /**
     * Convert this {@link WorldBBox2d} to {@link WorldBBox3d}, with additional components along the z-axis.
     *
     * @param originZ the z-coordinate of the starting position
     * @param sizeZ the size along the z-axis
     * @return a new {@link WorldBBox3d} with the current components along the x- and y-axes
     */
    public WorldBBox3d to3d(int originZ, int sizeZ) {
        return new WorldBBox3d(this, originZ, sizeZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldBBox2d that = (WorldBBox2d) o;
        return min.equals(that.min) && max.equals(that.max);
    }

    /**
     * Tells if box is empty.
     *
     * @return true if box is empty
     */
    public boolean isEmpty() {
        return size.x() == 0 || size.y() == 0;
    }

    /**
     * Returns intersection with another bounind box.
     *
     * @param box Other bounding box to intersect with
     *
     * @return A new bounding box representing the intersection (may be empty)
     */
    public WorldBBox2d intersection(WorldBBox2d box) {
        int minX = Math.max(min.x(), box.minX());
        int minY = Math.max(min.y(), box.minY());
        int maxX = Math.min(max.x(), box.maxX());
        int maxY = Math.min(max.y(), box.maxY());
        return new WorldBBox2d(minX, minY, Math.max(0, maxX - minX + 1), Math.max(0, maxY - minY + 1));
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorldBBox2d{min=%s, max=%s, size=%s}".formatted(min, max, size);
    }
}
