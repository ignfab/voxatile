package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * {@code Chunk2dElement} is returned by {@code Chunk2dIterator}.
 * It encapsulates coordinates (x, y) and associated {@code int} value.
 *
 * @see WorldCoords2d
 */
public class Chunk2dElement {
    private WorldCoords2d coords;
    private int value;

    /**
     * Constructs a new {Chunk2dElement}.
     *
     * @param coords the {@code WorldCoords2d}.
     * @param value  the value at coordinates.
     */
    protected Chunk2dElement(WorldCoords2d coords, int value) {
        this.coords = coords;
        this.value = value;
    }

    /**
     * Returns the value at coordinates.
     *
     * @return the value at coordinates.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the coordinates.
     *
     * @return the {@code WorldCoords2d}.
     */
    public WorldCoords2d getCoords() {
        return coords;
    }

    /**
     * Returns the x-coordinate.
     *
     * @return the x-coordinate value.
     */
    public int getX() {
        return coords.getX();
    }

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate value.
     */
    public int getY() {
        return coords.getY();
    }
}
