package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;

import static org.junit.jupiter.api.Assertions.*;

public class TestBufferedImageChunk {

    @Test
    public void testGetAndSetOutOfBounds() {
        WorldBBox2d bbox = new WorldBBox2d(-2, -1, 4, 3);
        BufferedImageChunk chunk = new BufferedImageChunk(bbox);

        assertThrows(IndexOutOfBoundsException.class,
            () -> chunk.set(3, 0, 0),
            "Wrong exception thrown when setting out of X bounds");

        assertThrows(IndexOutOfBoundsException.class,
            () -> chunk.set(-2, -2, 0),
            "Wrong exception thrown when setting out of Y bounds");
    }

    @Test
    public void testGetAndSetEveryWhere() {
        WorldBBox2d bbox = new WorldBBox2d(-5, -2, 4, 3);
        BufferedImageChunk chunk = new BufferedImageChunk(bbox);
        int v;

        // Set a different value on each chunk cell
        v = 0;
        for (int y = bbox.getMin().y(); y <= bbox.getMax().y(); y++)
            for (int x = bbox.getMin().x(); x <= bbox.getMax().x(); x++)
                chunk.set(x, y, v++);

        v = 0;
        for (int y = bbox.getMin().y(); y <= bbox.getMax().y(); y++)
            for (int x = bbox.getMin().x(); x <= bbox.getMax().x(); x++)
                assertEquals(chunk.get(x, y), v++, "("+x+", "+y+")");
    }

    @Test
    public void testGraphics2D() {
        WorldBBox2d bbox = new WorldBBox2d(2, 3, 4, 5);
        BufferedImageChunk chunk = new BufferedImageChunk(bbox);

        // Testing colors drawing two rectangles in a chunk:
        //
        //   X: 2 3 4 5
        // Y:  ---------
        // 3  | A A     |
        // 4  | A A     |
        // 5  | A B B B |
        // 6  |   B B B |
        // 7  |   B B B |
        //     ---------
        // Rectangle A has value 123
        // Rectangle B has value 234

        Graphics2D graphics = chunk.createGraphics();
        // Fill rectangle A
        graphics.setColor(chunk.colorFor(123));
        graphics.fillRect(0, 0, 2, 3);
        // Fill rectangle B
        graphics.setColor(chunk.colorFor(234));
        graphics.fillRect(1, 2, 3, 3);

        for (int x = 2; x < 6; x++)
            for (int y = 3; y < 8; y++)
                // Check rectangle B
                if (x >= 3 && y >= 5)
                    assertEquals(chunk.get(x, y), 234, "("+x+", "+y+")");
                // Check rectangle A
                else if (x <= 3 && y <= 5)
                    assertEquals(chunk.get(x, y), 123, "("+x+", "+y+")");
                // Check outside both rectangles
                else
                    assertEquals(chunk.get(x, y), 0, "("+x+", "+y+")");
    }
}
