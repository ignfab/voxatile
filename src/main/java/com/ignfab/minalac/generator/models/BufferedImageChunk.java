package com.ignfab.minalac.generator.models;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Arrays;

import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.WritableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorSkip;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A readable and writable 2d chunk of world map based on {@code BufferedImage}.
 *
 * Values are stored as bytes so their range is 0 to 255.
 */
public class BufferedImageChunk implements IterableChunk2d, WritableChunk2d {
    private BufferedImage image;
    private WorldBBox2d bbox;

    /**
     * Construct a new {@code BufferedImageChunk}
     *
     * @param bbox Bounding box of that chunk
     *
     * Chunk is initalized with 0 values.
     */
    BufferedImageChunk(WorldBBox2d bbox) {
        this.bbox = bbox;

        // This is a bit hacky. We use a 256 level grey image.
        // TYPE_BYTE_GRAY color model is the closest to what we need for now (store a few different values).
        // To get rid of this hack, we should have a look on how to develop a specific
        // color model to store integers (I guess this could be lot of useless work).
        image = new BufferedImage(bbox.getSize().getX(), bbox.getSize().getY(), BufferedImage.TYPE_BYTE_GRAY);
        Arrays.fill(((DataBufferByte) image.getRaster().getDataBuffer()).getData(), (byte)0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int get(int x, int y) {
        if (!bbox.contains(x, y)) throw new IndexOutOfBoundsException();
        return image.getRaster().getSample(x - bbox.getMin().getX(), y - bbox.getMin().getY(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int get(WorldCoords2d coords) {
        return get(coords.getX(), coords.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int x, int y, int value) {
        if (!bbox.contains(x, y)) throw new IndexOutOfBoundsException();
        image.getRaster().setSample(x - bbox.getMin().getX(), y - bbox.getMin().getY(), 0, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(WorldCoords2d coords, int value) {
        set(coords.getX(), coords.getY(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chunk2dIteratorSkip iterator() {
        return new Chunk2dIteratorSkip(this, 0);
    }

    /**
     * Creates a {@code Graphics2D} able to draw on this chunk.
     *
     * Coordinates in that {@code Graphics2D} are bounding box relative (starting at 0,0 and up to size).
     *
     * @return A {@code Graphics2D} instance linked to embedded {@code BufferedImage}.
     */
    public Graphics2D createGraphics() {
        return image.createGraphics();
    }

    // This method is very handy to definitely hide the TYPE_BYTE_GRAY hack:
    /**
     * Returns a {@code Color} for drawing a given integer value.
     *
     * @param value Value to be represented as color
     * @return A {@code Color} to be used to draw given value.
     */
    public static Color colorFor(int value) {
        if (value < 0 || value > 255)
            throw new IndexOutOfBoundsException();

        return new Color(value, value, value);
    }
}
