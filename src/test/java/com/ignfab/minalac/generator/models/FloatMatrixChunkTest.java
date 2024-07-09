package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.generation.CoordsConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.util.AffineTransformation;

public class FloatMatrixChunkTest {
    @Test
    public void testIndexes() throws TransformException {
        // Identity converter
        CoordsConverter converter = new CoordsConverter(IdentityTransform.create(2), new AffineTransformation());

        // Beware, Y is upside down in this matrix
        float[] data = {
            9.0f, 0.0f, 13.0f,
            7.0f, 0.0f, 0.0f,
            4.0f, 0.0f, 0.0f,
            1.0f, 2.0f, 3.0f
        };

        FloatMatrixChunk chunk = new FloatMatrixChunk(data, 3, 4, converter);

        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(-1, 0));
        assertEquals(1, chunk.get(0, 0));
        assertEquals(2, chunk.get(1, 0));
        assertEquals(3, chunk.get(2, 0));
        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(3, 0));

        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(0, -1));
        assertEquals(1, chunk.get(0, 0));
        assertEquals(4, chunk.get(0, 1));
        assertEquals(7, chunk.get(0, 2));
        assertEquals(9, chunk.get(0, 3));
        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(0, 4));

        assertEquals(13, chunk.get(2, 3));
    }

    @Test
    public void testExtrapolation() throws TransformException {
        CoordsConverter converter = new CoordsConverter(IdentityTransform.create(2), AffineTransformation.scaleInstance(10.0, 10.0));

        // Beware, Y is upside down in this matrix
        float[] data = {
            -1.0f, 1.0f,
            -3.0f, 5.0f,
        };

        FloatMatrixChunk chunk = new FloatMatrixChunk(data, 2, 2, converter);

        // Borders
        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(0, 11));
        assertEquals(FloatMatrixChunk.OUTSIDE, chunk.get(11, 0));

        assertEquals(-3, chunk.get(0, 0));
        assertEquals(-1, chunk.get(0, 10));
        assertEquals(5, chunk.get(10, 0));
        assertEquals(1, chunk.get(10, 10));

        // Centers
        assertEquals(1, chunk.get(5, 0));
        assertEquals(0, chunk.get(5, 10));
        assertEquals(-2, chunk.get(0, 5));
        assertEquals(3, chunk.get(10, 5));

        assertEquals(1, chunk.get(5, 5));
    }
}
