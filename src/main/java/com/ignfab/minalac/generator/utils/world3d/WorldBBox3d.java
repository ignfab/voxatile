package com.ignfab.minalac.generator.utils.world3d;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.iterator.WorldBBox3dIterator;

/**
 * The {@code WorldBBox3d} class represents a three-dimensional bounding box that surrounds an area of the voxel world.
 * This BBOX is defined by a minimum point and a maximum point. These points are included in the bounding box.
 * An explanation of the voxel world coordinates system can be found on {@link WorldCoords3d}.
 *
 * @see WorldCoords3d
 */
public class WorldBBox3d implements Bounded3d, Iterable<WorldCoords3d> {
    private final WorldCoords3d min;
    private final WorldCoords3d max;
    private final WorldSize3d size;

    /**
     * A reusable instance of {@link WorldBBox3d} that is empty.
     * The size of this bounding box is (0, 0, 0) and its origin is (0, 0, 0),
     * meaning its maximum point is (-1, -1, -1).
     */
    public static final WorldBBox3d EMPTY = new WorldBBox3d(0, 0, 0, 0, 0, 0);

    /**
     * A reusable instance of {@link WorldBBox3d} that contains only one voxel at (0, 0, 0).
     */
    public static final WorldBBox3d ORIGIN = new WorldBBox3d(0, 0, 0, 1, 1, 1);

    /**
     * Creates a new {@link WorldBBox3d} by providing a starting position and the desired size of the bounding box.
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
     * Creates a new {@link WorldBBox3d} by providing a starting position and the desired size of the bounding box.
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
     * Creates a new {@link WorldBBox3d} containing all given positions.
     *
     * @param positions a list of position coordinates that should be in resulting box
     */
    public WorldBBox3d(WorldCoords3d... positions) {
        if (positions.length == 0) {
            size = new WorldSize3d(0, 0, 0);
            min = new WorldCoords3d(0, 0, 0);
            max = new WorldCoords3d(0, 0, 0);
            return;
        }

        int minX = positions[0].x();
        int minY = positions[0].y();
        int minZ = positions[0].z();
        int maxX = positions[0].x();
        int maxY = positions[0].y();
        int maxZ = positions[0].z();

        for (int index = 1; index < positions.length; index++) {
            minX = Math.min(minX, positions[index].x());
            minY = Math.min(minY, positions[index].y());
            minZ = Math.min(minZ, positions[index].z());
            maxX = Math.max(maxX, positions[index].x());
            maxY = Math.max(maxY, positions[index].y());
            maxZ = Math.max(maxZ, positions[index].z());
        }

        min = new WorldCoords3d(minX, minY, minZ);
        max = new WorldCoords3d(maxX, maxY, maxZ);
        size = new WorldSize3d(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }

    /**
     * Creates a new {@link WorldBBox3d} from an existing {@link WorldBBox2d} and additional components along the z-axis.
     *
     * @param bbox an existing {@link WorldBBox2d} object
     * @param originZ the z-coordinate of the starting position
     * @param sizeZ the size along the z-axis
     */
    public WorldBBox3d(WorldBBox2d bbox, int originZ, int sizeZ) {
        this(bbox.min().to3d(originZ), bbox.size().to3d(sizeZ));
    }

    /**
     * Creates a new {@link WorldBBox2d} containing all bounded items.
     *
     * @param items iterable over bounded items to contain
     * @return a bounding box containing all items
     */
    public static WorldBBox3d surrounding(Iterable<? extends Bounded3d> items) {
        Iterator<? extends Bounded3d> iterator = items.iterator();

        WorldBBox3d bbox = EMPTY;

        while (iterator.hasNext() && bbox.isEmpty())
            bbox = iterator.next().bbox();

        if (!iterator.hasNext())
            return bbox;

        int minX = bbox.minX();
        int minY = bbox.minY();
        int minZ = bbox.minZ();
        int maxX = bbox.maxX();
        int maxY = bbox.maxY();
        int maxZ = bbox.maxZ();

        while (iterator.hasNext()) {
            bbox = iterator.next().bbox();
            if (bbox.isEmpty())
                continue;

            minX = Math.min(minX, bbox.minX());
            minY = Math.min(minY, bbox.minY());
            minZ = Math.min(minZ, bbox.minZ());
            maxX = Math.max(maxX, bbox.maxX());
            maxY = Math.max(maxY, bbox.maxY());
            maxZ = Math.max(maxZ, bbox.maxZ());
        }

        return new WorldBBox3d(minX, minY, minZ, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
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
        return minX() <= x && x <= maxX()
            && minY() <= y && y <= maxY()
            && minZ() <= z && z <= maxZ();
    }

    /**
     * Returns {@code true} if the given position is in the bounding box.
     *
     * @param position the position to be checked.
     * @return {@code true} if position is in the bounding box.
     */
    public boolean contains(Positioned3d position) {
        return contains(position.coords().x(), position.coords().y(), position.coords().z());
    }

    /**
     * Returns {@code true} if the given bbox is in this bounding box.
     *
     * @param other the bbox to be checked.
     * @return {@code true} if the provided bbox is in this bounding box.
     */
    public boolean contains(Bounded3d other) {
        return contains(other.bbox().min()) && contains(other.bbox().max());
    }

    /**
     * Tells if bounding box intersects another bounding box.
     *
     * @param other Other bounding box to test intersection with
     *
     * @return True if there is an intersection
     */
    public boolean intersects(WorldBBox3d other) {
        return minX() <= other.maxX()
            && minY() <= other.maxY()
            && minZ() <= other.maxZ()
            && maxX() >= other.minX()
            && maxY() >= other.minY()
            && maxZ() >= other.minZ();
    }

    /**
     * Returns intersection with another bounding box.
     *
     * @param other Other bounding box to intersect with
     *
     * @return A new bounding box representing the intersection (may be empty)
     */
    public WorldBBox3d intersection(WorldBBox3d other) {
        int minX = Math.max(minX(), other.minX());
        int minY = Math.max(minY(), other.minY());
        int minZ = Math.max(minZ(), other.minZ());
        int maxX = Math.min(maxX(), other.maxX());
        int maxY = Math.min(maxY(), other.maxY());
        int maxZ = Math.min(maxZ(), other.maxZ());
        return new WorldBBox3d(minX, minY, minZ, Math.max(0, maxX - minX + 1), Math.max(0, maxY - minY + 1), Math.max(0, maxZ - minZ + 1));
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
    public <T extends Positioned3d> Iterator<T> crop(Iterator<? extends T> iterator) {
        return Iterators.cast(Iterators.filter(iterator, this::contains));
    }

    /**
     * Same as {@link WorldBBox3d#crop(Iterator)}} but with an iterable as argument.
     *
     * @param iterable iterable giving an iterator over positioned items
     *
     * @return an iterator only with items contained in the bounding box
     *
     * @param <T> type of iterator results
     */
    public <T extends Positioned3d> Iterator<T> crop(Iterable<? extends T> iterable) {
        return crop(iterable.iterator());
    }

    /**
     * Returns the size of the bounding box.
     *
     * @return the size of the bounding box as a {@link WorldSize3d}.
     */
    public WorldSize3d size() {
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
     * Returns the bounding box size along the z-axis.
     *
     * @return the bounding box size along the z-axis.
     */
    public int sizeZ() {
        return size.z();
    }

    /**
     * Returns the minimum point.
     *
     * @return the {@link WorldCoords3d} of the minimum point.
     */
    public WorldCoords3d min() {
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
     * Returns the minimum point z-coordinate.
     *
     * @return the z-coordinate of the minimum point.
     */
    public int minZ() {
        return min.z();
    }

    /**
     * Returns the maximum point.
     *
     * @return the {@link WorldCoords3d} of the maximum point.
     */
    public WorldCoords3d max() {
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
     * Returns the maximum point z-coordinate.
     *
     * @return the z-coordinate of the maximum point.
     */
    public int maxZ() {
        return max.z();
    }

    /**
     * Returns the bounding box shifted by given coordinates.
     *
     * @param by coordinate to shift the bounding box by
     * @return shifted bounding box
     */
    public WorldBBox3d shift(WorldCoords3d by) {
        return new WorldBBox3d(min().add(by), size());
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

    /**
     * Tells if box is empty.
     *
     * @return true if box is empty
     */
    public boolean isEmpty() {
        return size.x() == 0 || size.y() == 0 || size.z() == 0;
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

        WorldBBox3d that = (WorldBBox3d) o;
        return min.equals(that.min) && max.equals(that.max);
    }

    @Override
    public String toString() {
        return "WorldBBox3d{min=%s, max=%s, size=%s}".formatted(min, max, size);
    }

    @Override
    public WorldBBox3d bbox() {
        return this;
    }

    /**
     * Returns the center of this {@link WorldBBox3d}.
     * @return the coordinates of the center.
     */
    public WorldCoords3d center() {
        return new WorldCoords3d(
            min.x() + size.x() / 2,
            min.y() + size.y() / 2,
            min.z() + size.z() / 2
        );
    }
}
