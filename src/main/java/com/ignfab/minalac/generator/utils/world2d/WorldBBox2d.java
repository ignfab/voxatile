package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world2d.iterator.WorldBBox2dIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import java.util.Iterator;

/**
 * The {@code WorldBBox2d} class represents a two-dimensional bounding box that surrounds an area on the surface (xy-plane) of the voxel world.
 * This BBOX is defined by a minimum point and a maximum point. These points are included in the bounding box.
 * An explanation of the voxel world coordinates system can be found on {@link com.ignfab.minalac.generator.utils.world2d.WorldCoords2d}.
 *
 * @see WorldCoords2d
 */
public class WorldBBox2d implements Bounded2d, Iterable<WorldCoords2d> {
    private final WorldCoords2d min;
    private final WorldCoords2d max;
    private final WorldSize2d size;

    /**
     * A reusable instance of {@link WorldBBox2d} that is empty.
     * The size of this bounding box is (0, 0) and its origin is (0, 0),
     * meaning its maximum point is (-1, -1).
     */
    public static final WorldBBox2d EMPTY = new WorldBBox2d(0, 0, 0, 0);

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
     * Creates a new {@link WorldBBox2d} containing all bounded items.
     *
     * @param items iterable over bounded items to contain
     * @return a bounding box containing all items
     */
    public static WorldBBox2d surrounding(Iterable<? extends Bounded2d> items) {
        Iterator<? extends Bounded2d> iterator = items.iterator();

        WorldBBox2d bbox = EMPTY;

        while (iterator.hasNext() && bbox.isEmpty())
            bbox = iterator.next().bbox();

        if (!iterator.hasNext())
            return bbox;

        int minX = bbox.minX();
        int minY = bbox.minY();
        int maxX = bbox.maxX();
        int maxY = bbox.maxY();

        while (iterator.hasNext()) {
            bbox = iterator.next().bbox();
            if (bbox.isEmpty())
                continue;

            minX = Math.min(minX, bbox.minX());
            minY = Math.min(minY, bbox.minY());
            maxX = Math.max(maxX, bbox.maxX());
            maxY = Math.max(maxY, bbox.maxY());
        }

        return new WorldBBox2d(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    /**
     * Returns {@code true} if the given coordinates are in the bounding box.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @return {@code true} if coordinates are in the bounding box.
     */
    public boolean contains(int x, int y) {
        return minX() <= x && x <= maxX()
            && minY() <= y && y <= maxY();
    }

    /**
     * Returns {@code true} if the given position is in the bounding box.
     *
     * @param coords the position to be checked.
     * @return {@code true} if posiiton is in the bounding box.
     */
    public boolean contains(WorldCoords2d coords) {
        return contains(coords.x(), coords.y());
    }

    /**
     * Returns {@code true} if the given bbox is in this bounding box.
     *
     * @param other the bbox to be checked.
     * @return {@code true} if the provided bbox is in this bounding box.
     */
    public boolean contains(Bounded2d other) {
        return contains(other.bbox().min()) && contains(other.bbox().max());
    }

    /**
     * Tells if bounding box intersects another bounind box.
     *
     * @param other Other bounding box to test intersection with
     *
     * @return True if there is an intersection
     */
    public boolean intersects(WorldBBox2d other) {
        return minX() <= other.maxX()
            && minY() <= other.maxY()
            && maxX() >= other.minX()
            && maxY() >= other.minY();
    }

    /**
     * Returns intersection with another bounind box.
     *
     * @param other Other bounding box to intersect with
     *
     * @return A new bounding box representing the intersection (may be empty)
     */
    public WorldBBox2d intersection(WorldBBox2d other) {
        int minX = Math.max(minX(), other.minX());
        int minY = Math.max(minY(), other.minY());
        int maxX = Math.min(maxX(), other.maxX());
        int maxY = Math.min(maxY(), other.maxY());
        return new WorldBBox2d(minX, minY, Math.max(0, maxX - minX + 1), Math.max(0, maxY - minY + 1));
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

    /**
     * Tells if box is empty.
     *
     * @return true if box is empty
     */
    public boolean isEmpty() {
        return size.x() == 0 || size.y() == 0;
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldBBox2d that = (WorldBBox2d) o;
        return min.equals(that.min) && max.equals(that.max);
    }

    @Override
    public String toString() {
        return "WorldBBox2d{min=%s, max=%s, size=%s}".formatted(min, max, size);
    }

    @Override
    public WorldBBox2d bbox() {
        return this;
    }
}
