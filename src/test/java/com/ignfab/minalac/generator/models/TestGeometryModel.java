package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.world2d.chunk.ReadableChunk2d;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.util.AffineTransformation;

import static org.junit.jupiter.api.Assertions.*;

public class TestGeometryModel {
    private static CoordsConverter converter = new CoordsConverter(IdentityTransform.create(2), new AffineTransformation());
    private static GeometryFactory factory = new GeometryFactory();

    @Test
    public void testPointRasterization() throws TransformException {
        System.out.println("testPointRasterization");
        GeometryModel model = new GeometryModel(factory.createPoint(new Coordinate(10.0, -20.0)), converter);
        ReadableChunk2d chunk = model.getChunk();

        assertEquals(chunk.bbox().getSize().x(),  1, "x-size");
        assertEquals(chunk.bbox().getSize().y(),  1, "y-size");
        assertEquals(chunk.bbox().getMin().x(),  10, "x-min");
        assertEquals(chunk.bbox().getMin().y(), -20, "y-min");
    }

    @Test
    public void testLineStringRasterization() throws TransformException {
        Coordinate[] coords = {
            new Coordinate(-1.0, -1.0),
            new Coordinate(-1.0,  1.0),
            new Coordinate( 2.0,  1.0)};
        GeometryModel model = new GeometryModel(factory.createLineString(coords), converter);
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
    public void testPolygonRasterization() throws TransformException {
        Coordinate[] coords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)};
        GeometryModel model = new GeometryModel(factory.createPolygon(coords), converter);
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
    public void testPolygonWithHoleRasterization() throws TransformException {
        Coordinate[] shapecoords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate(-2.0,  2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)};
        Coordinate[] holecoords = {
            new Coordinate(-2.0, -2.0),
            new Coordinate( 2.0,  2.0),
            new Coordinate( 2.0, -2.0),
            new Coordinate(-2.0, -2.0)};

        LinearRing[] holes = { (LinearRing)factory.createLinearRing(holecoords) };
        LinearRing shape = (LinearRing)factory.createLinearRing(shapecoords);

        GeometryModel model = new GeometryModel(factory.createPolygon(shape, holes), converter);
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
        assertEquals(chunk.get( 0,  2), GeometryModel.BORDER,  "(0, 2)" );
        assertEquals(chunk.get( 1,  2), GeometryModel.BORDER,  "(1, 2)");
        assertEquals(chunk.get( 2,  2), GeometryModel.BORDER,  "(2, 2)" );
    }
}
