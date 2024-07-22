package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.util.AffineTransformation;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("checkstyle:ParenPad")
public class GeometryModelTest {
    private static final CoordsConverter CONVERTER = new CoordsConverter(IdentityTransform.create(2), new AffineTransformation());
    private static final GeometryFactory FACTORY = new GeometryFactory();
    private static final WorldBBox2d LARGEBOX = new WorldBBox2d(-100, -100, 200, 200);

    @Test
    @DisplayName("Test point geometry rasterization")
    public void testGetChunkPoint() throws TransformException {
        GeometryModel model = new GeometryModel(FACTORY.createPoint(new Coordinate(10.0, -20.0)), CONVERTER, LARGEBOX);
        ReadableChunk2d chunk = model.getChunk();

        assertEquals(chunk.bbox().getSize().x(),  1, "x-size");
        assertEquals(chunk.bbox().getSize().y(),  1, "y-size");
        assertEquals(chunk.bbox().getMin().x(),  10, "x-min");
        assertEquals(chunk.bbox().getMin().y(), -20, "y-min");
    }

    @Test
    @DisplayName("Test linestring geometry rasterization")
    public void testGetChunkLineString() throws TransformException {
        Coordinate[] coords = {
            new Coordinate(-1.0, -1.0),
            new Coordinate(-1.0,  1.0),
            new Coordinate( 2.0,  1.0)
        };
        GeometryModel model = new GeometryModel(FACTORY.createLineString(coords), CONVERTER, LARGEBOX);
        ReadableChunk2d chunk = model.getChunk();

        // Expected :
        // B O O O
        // B O O O
        // B B B B

        // Bbox check
        assertEquals(chunk.bbox().getSize().x(), 4, "x-size");
        assertEquals(chunk.bbox().getSize().y(), 3, "y-size");
        assertEquals(chunk.bbox().getMin().x(), -1, "x-min");
        assertEquals(chunk.bbox().getMin().y(), -1, "y-min");

        // Pixel check
        assertEquals(chunk.get(-1, -1), GeometryModel.BORDER,  "(-1, -1)");
        assertEquals(chunk.get( 0, -1), GeometryModel.OUTSIDE, "(0, -1)");
        assertEquals(chunk.get( 1, -1), GeometryModel.OUTSIDE, "(1, -1)");
        assertEquals(chunk.get( 2, -1), GeometryModel.OUTSIDE, "(2, -1)");
        assertEquals(chunk.get(-1,  0), GeometryModel.BORDER,  "(-1, 0)");
        assertEquals(chunk.get( 0,  0), GeometryModel.OUTSIDE, "(0, 0)");
        assertEquals(chunk.get( 1,  0), GeometryModel.OUTSIDE, "(1, 0)");
        assertEquals(chunk.get( 2,  0), GeometryModel.OUTSIDE, "(2, 0)");
        assertEquals(chunk.get(-1,  1), GeometryModel.BORDER,  "(-1, 1)");
        assertEquals(chunk.get( 0,  1), GeometryModel.BORDER,  "(0, 1)");
        assertEquals(chunk.get( 1,  1), GeometryModel.BORDER,  "(1, 1)");
        assertEquals(chunk.get( 2,  1), GeometryModel.BORDER,  "(2, 1)");
    }

    @Test
    @DisplayName("Test polygon geometry rasterization")
    public void testGetChunkPolygon() throws TransformException {
        Coordinate[] coords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)};
        GeometryModel model = new GeometryModel(FACTORY.createPolygon(coords), CONVERTER, LARGEBOX);
        ReadableChunk2d chunk = model.getChunk();

        // Expected :
        // 1 1 1 1 1
        // 0 1 2 2 1
        // 0 0 1 2 1
        // 0 0 0 1 1
        // 0 0 0 0 1

        // Bbox check
        assertEquals(chunk.bbox().getSize().x(), 5, "x-size");
        assertEquals(chunk.bbox().getSize().y(), 5, "y-size");
        assertEquals(chunk.bbox().getMin().x(), -2, "x-min");
        assertEquals(chunk.bbox().getMin().y(), -2, "y-min");

        // Pixel check
        assertEquals(chunk.get(-2, -2), GeometryModel.BORDER,  "(-2,-2)");
        assertEquals(chunk.get(-1, -2), GeometryModel.BORDER,  "(-1, -2)");
        assertEquals(chunk.get( 0, -2), GeometryModel.BORDER,  "(0, -2)");
        assertEquals(chunk.get( 1, -2), GeometryModel.BORDER,  "(1, -2)");
        assertEquals(chunk.get( 2, -2), GeometryModel.BORDER,  "(2, -2)");

        assertEquals(chunk.get(-2, -1), GeometryModel.OUTSIDE, "(-2, -1)");
        assertEquals(chunk.get(-1, -1), GeometryModel.BORDER,  "(-1, -1)");
        assertEquals(chunk.get( 0, -1), GeometryModel.INSIDE,  "(0, -1)");
        assertEquals(chunk.get( 1, -1), GeometryModel.INSIDE,  "(1, -1)");
        assertEquals(chunk.get( 2, -1), GeometryModel.BORDER,  "(2, -1)");

        assertEquals(chunk.get(-2,  0), GeometryModel.OUTSIDE, "(-2, 0)");
        assertEquals(chunk.get(-1,  0), GeometryModel.OUTSIDE, "(-1, 0)");
        assertEquals(chunk.get( 0,  0), GeometryModel.BORDER,  "(0, 0)");
        assertEquals(chunk.get( 1,  0), GeometryModel.INSIDE,  "(1, 0)");
        assertEquals(chunk.get( 2,  0), GeometryModel.BORDER,  "(2, 0)");

        assertEquals(chunk.get(-2,  1), GeometryModel.OUTSIDE, "(-2, 1)");
        assertEquals(chunk.get(-1,  1), GeometryModel.OUTSIDE, "(-1, 1)");
        assertEquals(chunk.get( 0,  1), GeometryModel.OUTSIDE, "(0, 1)");
        assertEquals(chunk.get( 1,  1), GeometryModel.BORDER,  "(1, 1)");
        assertEquals(chunk.get( 2,  1), GeometryModel.BORDER,  "(2, 1)");

        assertEquals(chunk.get(-2,  2), GeometryModel.OUTSIDE, "(-2, 2)");
        assertEquals(chunk.get(-1,  2), GeometryModel.OUTSIDE, "(-1, 2)");
        assertEquals(chunk.get( 0,  2), GeometryModel.OUTSIDE, "(0, 2)");
        assertEquals(chunk.get( 1,  2), GeometryModel.OUTSIDE, "(1, 2)");
        assertEquals(chunk.get( 2,  2), GeometryModel.BORDER,  "(2, 2)");
    }

    @Test
    @DisplayName("Test polygon with hole geometry rasterization")
    public void testGetChunkPolygonWithHole() throws TransformException {
        Coordinate[] shapecoords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate(-2.0,  2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)
        };
        Coordinate[] holecoords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)
        };

        LinearRing[] holes = { FACTORY.createLinearRing(holecoords) };
        LinearRing shape = FACTORY.createLinearRing(shapecoords);

        GeometryModel model = new GeometryModel(FACTORY.createPolygon(shape, holes), CONVERTER, LARGEBOX);
        ReadableChunk2d chunk = model.getChunk();

        // Expected :
        // 1 1 1 1 1
        // 1 1 0 0 1
        // 1 2 1 0 1
        // 1 2 2 1 1
        // 1 1 1 1 1

        // Bbox check
        assertEquals(chunk.bbox().getSize().x(), 5, "x-size");
        assertEquals(chunk.bbox().getSize().y(), 5, "y-size");
        assertEquals(chunk.bbox().getMin().x(), -2, "x-min");
        assertEquals(chunk.bbox().getMin().y(), -2, "y-min");

        // Pixel check
        assertEquals(chunk.get(-2, -2), GeometryModel.BORDER,  "(-2,-2)");
        assertEquals(chunk.get(-1, -2), GeometryModel.BORDER,  "(-1, -2)");
        assertEquals(chunk.get( 0, -2), GeometryModel.BORDER,  "(0, -2)");
        assertEquals(chunk.get( 1, -2), GeometryModel.BORDER,  "(1, -2)");
        assertEquals(chunk.get( 2, -2), GeometryModel.BORDER,  "(2, -2)");

        assertEquals(chunk.get(-2, -1), GeometryModel.BORDER,  "(-2, -1)");
        assertEquals(chunk.get(-1, -1), GeometryModel.BORDER,  "(-1, -1)");
        assertEquals(chunk.get( 0, -1), GeometryModel.OUTSIDE, "(0, -1)");
        assertEquals(chunk.get( 1, -1), GeometryModel.OUTSIDE, "(1, -1)");
        assertEquals(chunk.get( 2, -1), GeometryModel.BORDER,  "(2, -1)");

        assertEquals(chunk.get(-2,  0), GeometryModel.BORDER,  "(-2, 0)");
        assertEquals(chunk.get(-1,  0), GeometryModel.INSIDE,  "(-1, 0)");
        assertEquals(chunk.get( 0,  0), GeometryModel.BORDER,  "(0, 0)");
        assertEquals(chunk.get( 1,  0), GeometryModel.OUTSIDE, "(1, 0)");
        assertEquals(chunk.get( 2,  0), GeometryModel.BORDER,  "(2, 0)");

        assertEquals(chunk.get(-2,  1), GeometryModel.BORDER,  "(-2, 1)");
        assertEquals(chunk.get(-1,  1), GeometryModel.INSIDE,  "(-1, 1)");
        assertEquals(chunk.get( 0,  1), GeometryModel.INSIDE,  "(0, 1)");
        assertEquals(chunk.get( 1,  1), GeometryModel.BORDER,  "(1, 1)");
        assertEquals(chunk.get( 2,  1), GeometryModel.BORDER,  "(2, 1)");

        assertEquals(chunk.get(-2,  2), GeometryModel.BORDER,  "(-2, 2)");
        assertEquals(chunk.get(-1,  2), GeometryModel.BORDER,  "(-1, 2)");
        assertEquals(chunk.get( 0,  2), GeometryModel.BORDER,  "(0, 2)");
        assertEquals(chunk.get( 1,  2), GeometryModel.BORDER,  "(1, 2)");
        assertEquals(chunk.get( 2,  2), GeometryModel.BORDER,  "(2, 2)");
    }

    @Test
    @DisplayName("Test rasterization when intersecting with small box")
    public void testGetChunkSmallBox() throws TransformException {
        Coordinate[] coords = {
            new Coordinate(-1.0,  1.0),
            new Coordinate(-1.0, -5.0),
            new Coordinate(-4.0, -2.0),
            new Coordinate(-1.0,  1.0)};
        GeometryModel model = new GeometryModel(
            FACTORY.createPolygon(coords), CONVERTER, new WorldBBox2d(-3, -3, 5, 4));
        ReadableChunk2d chunk = model.getChunk();

        // This is what we have drawn:
        // X = Four points of the polygon
        // x = Rest of the polygon edge
        // o = Inside of the polygon
        // [] = Marks the limit bounding box

        //     -4 -3 -2 -1  0  1
        //   1           X
        //   0    [   x  x      ]
        //  -1    [x  o  x      ]
        //  -2  X [o  o  x      ]
        //  -3    [x  o  x      ]
        //  -4        x  x
        //  -5           X

        // Bbox check
        assertEquals( 3, chunk.bbox().getSize().x(), "x-size"); // Intersection only
        assertEquals( 4, chunk.bbox().getSize().y(), "y-size");
        assertEquals(-3, chunk.bbox().getMin().x(),  "x-min");
        assertEquals(-3, chunk.bbox().getMin().y(),  "y-min");

        // Pixel check
        assertEquals(GeometryModel.OUTSIDE, chunk.get(-3,  0), "(-3, 0)");
        assertEquals(GeometryModel.BORDER,  chunk.get(-2,  0), "(-2, 0)");
        assertEquals(GeometryModel.BORDER,  chunk.get(-1,  0), "(-1, 0)");

        assertEquals(GeometryModel.BORDER,  chunk.get(-3, -1), "(-3, -1)");
        assertEquals(GeometryModel.INSIDE,  chunk.get(-2, -1), "(-2, -1)");
        assertEquals(GeometryModel.BORDER,  chunk.get(-1, -1), "(-1, -1)");

        assertEquals(GeometryModel.INSIDE,  chunk.get(-3, -2), "(-3, -2)");
        assertEquals(GeometryModel.INSIDE,  chunk.get(-2, -2), "(-2, -2)");
        assertEquals(GeometryModel.BORDER,  chunk.get(-1, -2), "(-1, -2)");

        assertEquals(GeometryModel.BORDER,  chunk.get(-3, -3), "(-3, -3)");
        assertEquals(GeometryModel.INSIDE,  chunk.get(-2, -3), "(-2, -3)");
        assertEquals(GeometryModel.BORDER,  chunk.get(-1, -3), "(-1, -3)");
    }
}
