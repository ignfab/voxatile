package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world2d.iterator.WorldBBox2dIterator;

/**
 * The {@code WorldBBox2d} class represents a two-dimensional bounding box that surrounds an area on the surface of (xy-plane) of the voxel world.
 * This BBOX is defined by a minimum point and a maximum point. These points are included in the bounding box.
 * An explanation of the voxel world coordinates system can be found on {@link com.ignfab.minalac.generator.utils.world2d.WorldCoords2d}.
 *
 * @see WorldCoords2d
 */
public class WorldBBox2d {
    private WorldCoords2d min, max;
    private WorldSize2d size;

    /**
     * Constructs a new {@code WorldBBox2d} by providing a starting position and the desired size of the bounding box.
     *
     * @param origin the starting position's coordinates (minimum point).
     * @param size   the bounding box size.
     */
    public WorldBBox2d(WorldCoords2d origin, WorldSize2d size) {
        this.size = size;
        min = origin;
        max = new WorldCoords2d(
                min.getX() + size.getX() - 1,
                min.getY() + size.getY() - 1
        );
    }

    /**
     * Constructs a new {@code WorldBBox2d} by providing a starting position and the desired size of the bounding box.
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
     * Constructs a new {@code WorldBBox2d} by providing the minimum and maximum points of the bounding box.
     *
     * @param min the minimum point's coordinates.
     * @param max the maximum point's coordinates.
     * @throws IllegalArgumentException if any coordinates of the maximum point is less than its counterpart in the minimum point.
     */
    public WorldBBox2d(WorldCoords2d min, WorldCoords2d max) {
        if (min.getX() > max.getX() || min.getY() > max.getY())
            throw new IllegalArgumentException("Minimum point must be less than or equal to maximum point");
        this.min = min;
        this.max = max;
        size = new WorldSize2d(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1);
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(int x, int y) {
        return min.getX() <= x && x <= max.getX() && min.getY() <= y && y <= max.getY();
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param coords the coordinates to be checked.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(WorldCoords2d coords) {
        return contains(coords.getX(), coords.getY());
    }

    /**
     * Returns the size of the bounding box.
     *
     * @return the size of the bounding box as a {@code WorldSize2d}.
     */
    public WorldSize2d getSize() {
        return size;
    }

    /**
     * Returns the minimum point.
     *
     * @return the {@code WorldCoords2d} of the minimum point.
     */
    public WorldCoords2d getMin() {
        return min;
    }

    /**
     * Returns the maximum point.
     *
     * @return the {@code WorldCoords2d} of the maximum point.
     */
    public WorldCoords2d getMax() {
        return max;
    }

    /**
     * Returns an iterator.
     *
     * @return a {@code WorldBBox2dIterator} to iterate over all the points contained in the bounding box.
     */
    public WorldBBox2dIterator iterator() {
        return new WorldBBox2dIterator(this);
    }
}