package com.ignfab.minalac.generator.utils.world2d;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.iterator.WorldBBox2dIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

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
     * A reusable instance of {@link WorldBBox2d} that contains only one voxel at (0, 0).
     */
    public static final WorldBBox2d ORIGIN = new WorldBBox2d(0, 0, 1, 1);

    /**
     * A reusable instance of {@link WorldBBox2d} that is huge enough to be considered as infinite.
     */
    // TODO: A proper INFINITE implementation should be written.
    public static final WorldBBox2d INFINITE = new WorldBBox2d(-1_000_000, -1_000_000, 2_000_000, 2_000_000);

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
     * @param positions a list of position coordinates that should be in resulting box
     */
    public WorldBBox2d(WorldCoords2d... positions) {
        if (positions.length == 0) {
            size = new WorldSize2d(0, 0);
            min = new WorldCoords2d(0, 0);
            max = new WorldCoords2d(0, 0);
            return;
        }

        int minX = positions[0].x();
        int minY = positions[0].y();
        int maxX = positions[0].x();
        int maxY = positions[0].y();

        for (int index = 1; index < positions.length; index++) {
            minX = Math.min(minX, positions[index].x());
            minY = Math.min(minY, positions[index].y());
            maxX = Math.max(maxX, positions[index].x());
            maxY = Math.max(maxY, positions[index].y());
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
     * @param position the position to be checked.
     * @return {@code true} if position is in the bounding box.
     */
    public boolean contains(Positioned2d position) {
        return contains(position.coords().x(), position.coords().y());
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
     * Tells if bounding box intersects another bounding box.
     *
     * @param other Other bounding box to test intersection with
     *
     * @return {@code true} if there is an intersection
     */
    public boolean intersects(WorldBBox2d other) {
        return minX() <= other.maxX()
            && minY() <= other.maxY()
            && maxX() >= other.minX()
            && maxY() >= other.minY();
    }

    /**
     * Tells if bounding box intersects a 3d bounding box.
     *
     * @param other Other bounding box to intersect with
     *
     * @return {@code true} if there is an intersection
     */
    public boolean intersects(WorldBBox3d other) {
        return minX() <= other.maxX()
            && minY() <= other.maxY()
            && maxX() >= other.minX()
            && maxY() >= other.minY();
    }

    /**
     * Returns intersection with another bounding box.
     *
     * @param other Other bounding box to intersect with
     *
     * @return A new bounding box representing the intersection (which may be empty)
     */
    public WorldBBox2d intersection(WorldBBox2d other) {
        int minX = Math.max(minX(), other.minX());
        int minY = Math.max(minY(), other.minY());
        int maxX = Math.min(maxX(), other.maxX());
        int maxY = Math.min(maxY(), other.maxY());
        return new WorldBBox2d(minX, minY, Math.max(0, maxX - minX + 1), Math.max(0, maxY - minY + 1));
    }

    /**
     * Returns intersection with a 3d bounding box.
     *
     * @param other Other bounding box to intersect with
     *
     * @return A new bounding box representing the intersection (which may be empty)
     */
    public WorldBBox2d intersection(WorldBBox3d other) {
        return intersection(other.to2d());
    }

    /**
     * Crops an iterator over positioned items to the bounding box.
     *
     * @param iterator iterator over positioned items
     *
     * @return an iterator only with items contained in the bounding box
     *
     * @param <T> type of iterator results
     */
    public <T extends Positioned2d> Iterator<T> crop(Iterator<? extends T> iterator) {
        return Iterators.cast(Iterators.filter(iterator, this::contains));
    }

    /**
     * Same as {@link WorldBBox2d#crop(Iterator)}} but with an iterable as argument.
     *
     * @param iterable iterable giving an iterator over positioned items
     *
     * @return an iterator only with items contained in the bounding box
     *
     * @param <T> type of iterator results
     */
    public <T extends Positioned2d> Iterator<T> crop(Iterable<? extends T> iterable) {
        return crop(iterable.iterator());
    }

    /**
     * {@return the size of the bounding box}
     */
    public WorldSize2d size() {
        return size;
    }

    /**
     * {@return the bounding box size along the x-axis}
     */
    public int sizeX() {
        return size.x();
    }

    /**
     * {@return the bounding box size along the y-axis}
     */
    public int sizeY() {
        return size.y();
    }

    /**
     * {@return the minimum point}
     */
    public WorldCoords2d min() {
        return min;
    }

    /**
     * {@return the x-coordinate of the minimum point}
     */
    public int minX() {
        return min.x();
    }

    /**
     * {@return the y-coordinate of the minimum point}
     */
    public int minY() {
        return min.y();
    }

    /**
     * {@return the maximum point}
     */
    public WorldCoords2d max() {
        return max;
    }

    /**
     * {@return the x-coordinate of the maximum point}
     */
    public int maxX() {
        return max.x();
    }

    /**
     * {@return the y-coordinate of the maximum point}
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

    /**
     * Returns the center of this {@link WorldBBox2d}.
     * @return the coordinates of the center.
     */
    public WorldCoords2d center() {
        return new WorldCoords2d(
            min.x() + size.x() / 2,
            min.y() + size.y() / 2
        );
    }
}
