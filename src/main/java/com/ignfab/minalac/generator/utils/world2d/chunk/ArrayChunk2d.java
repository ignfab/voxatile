package com.ignfab.minalac.generator.utils.world2d.chunk;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;

import java.util.Arrays;

/**
 * The {@code ArrayChunk2d} class represents a readable and writable two-dimensional chunk of the voxel world based on an array.
 * It implements {@link WritableChunk2d} and {@link ReadableChunk2d} interfaces.
 */
public class ArrayChunk2d implements ReadableChunk2d, WritableChunk2d {
    /**
     * The bounding box of the chunk.
     */
    protected WorldBBox2d bbox;
    /**
     * The array used to store the values.
     */
    protected int[] values;

    /**
     * Constructs a new {@code ArrayChunk2d}.
     *
     * @param bbox         the bounding box of the chunk.
     * @param defaultValue the defaultValue associated with all the points.
     */
    public ArrayChunk2d(WorldBBox2d bbox, int defaultValue) {
        this.bbox = bbox;
        values = new int[bbox.getSize().getX() * bbox.getSize().getY()];
        Arrays.fill(values, defaultValue);
    }

    /**
     * Constructs a new {@code ArrayChunk2d}.
     *
     * @param originX      the x-coordinate of the starting position.
     * @param originY      the y-coordinate of the starting position.
     * @param sizeX        the size along the x-axis of the {@code ArrayChunk2d}.
     * @param sizeY        the size along the y-axis of the {@code ArrayChunk2d}.
     * @param defaultValue the defaultValue associated with all the points.
     * @throws IllegalArgumentException if either {@code sizeX} or {@code sizeY} is less than or equal to 0.
     */
    public ArrayChunk2d(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
        this(new WorldBBox2d(originX, originY, sizeX, sizeY), defaultValue);
    }

    /**
     * Constructs a new {@code ArrayChunk2d}.
     *
     * @param coords       the coordinate of the starting position.
     * @param size         the size of the {@code ArrayChunk2d}.
     * @param defaultValue the defaultValue associated with all the points.
     */
    public ArrayChunk2d(WorldCoords2d coords, WorldSize2d size, int defaultValue) {
        this(new WorldBBox2d(coords, size), defaultValue);
    }

    private int index(int x, int y) {
        if (!bbox.contains(x, y))
            throw new IndexOutOfBoundsException(String.format("%s: Index out of range at (x=%d, y=%d)", getClass().getSimpleName(), x, y));
        return (x - bbox.getMin().getX()) * bbox.getSize().getY() + (y - bbox.getMin().getY());
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    @Override
    public void set(int x, int y, int value) {
        values[index(x, y)] = value;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    @Override
    public void set(WorldCoords2d coords, int value) {
        values[index(coords.getX(), coords.getY())] = value;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    @Override
    public int get(int x, int y) {
        return values[index(x, y)];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    @Override
    public int get(WorldCoords2d coords) {
        return values[index(coords.getX(), coords.getY())];
    }
}
