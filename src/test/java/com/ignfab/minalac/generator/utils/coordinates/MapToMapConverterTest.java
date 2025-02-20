package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;

import static org.junit.jupiter.api.Assertions.*;

public class MapToMapConverterTest {

    private static final GeometryFactory FACTORY = new GeometryFactory();
    private static final AffineTransformation ATID = new AffineTransformation();

    @Test
    @DisplayName("Post transformation tests")
    public void testPointPostTransform() throws TransformException {
        Geometry result;
        MathTransform mtid = IdentityTransform.create(2);

        // Verify identity transformation
        result = new Converter(ATID, mtid, ATID)
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(),  1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), -2.0, 0.0001);

        // Verify 180° rotation transformation
        result = new Converter(ATID, mtid, AffineTransformation.rotationInstance(Math.PI))
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), -1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(),  2.0, 0.0001);

        // Verify 90° rotation transformation
        result = new Converter(ATID, mtid, AffineTransformation.rotationInstance(Math.PI / 2))
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), 2.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), 1.0, 0.0001);

        // Verify translation transformation
        result = new Converter(ATID, mtid, AffineTransformation.translationInstance(-2.0, 1.0))
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), -1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), -1.0, 0.0001);
    }

    @Test
    @DisplayName("Pre transformation tests")
    public void testPointPreTransform() throws TransformException {
        Geometry result;
        MathTransform mtid = IdentityTransform.create(2);

        // Verify 180° rotation transformation
        result = new Converter(AffineTransformation.rotationInstance(Math.PI), mtid, ATID)
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(-1.0, result.getCoordinate().getX(), 0.0001);
        assertEquals(2.0, result.getCoordinate().getY(), 0.0001);

        // Verify 90° rotation transformation
        result = new Converter(AffineTransformation.rotationInstance(Math.PI / 2), mtid, ATID)
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(2.0, result.getCoordinate().getX(), 0.0001);
        assertEquals(1.0, result.getCoordinate().getY(), 0.0001);

        // Verify translation transformation
        result = new Converter(AffineTransformation.translationInstance(-2.0, 1.0), mtid, ATID)
            .convert(FACTORY.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(-1.0, result.getCoordinate().getX(), 0.0001);
        assertEquals(-1.0, result.getCoordinate().getY(), 0.0001);
    }

    @Test
    @DisplayName("Tests transformation works as expected on multipoint shapes")
    public void testShapeAffineTransformation() throws TransformException {
        Geometry result;

        Coordinate[] coords = {new Coordinate(-2.0, -2.0), new Coordinate(-1.0, 2.0), new Coordinate(2.0, 0.0)};

        result = new Converter(new AffineTransformation(), IdentityTransform.create(2), AffineTransformation.rotationInstance(Math.PI / 2))
            .convert(FACTORY.createLineString(coords));

        coords = result.getCoordinates();

        // We assume that coords order has not changed but this is not sure at all.
        assertEquals(2.0, coords[0].getX(), 0.0001);
        assertEquals(-2.0, coords[0].getY(), 0.0001);
        assertEquals(-2.0, coords[1].getX(), 0.0001);
        assertEquals(-1.0, coords[1].getY(), 0.0001);
        assertEquals(0.0, coords[2].getX(), 0.0001);
        assertEquals(2.0, coords[2].getY(), 0.0001);
    }

    @Test
    @DisplayName("Testing projection works as expected, combined or not with affine transformations")
    public void testProjection() throws TransformException, FactoryException {
        Geometry result;

        // Check simple geographic conversion from WSG 84 to LAMBERT 93
        // IGN leveling mark https://geodesie.ign.fr/fiches/pdf/P.C.K3L3-20b_534424.pdf
        MathTransform crsTransform = CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:2154"));
        result = new Converter(ATID, crsTransform, ATID)
            .convert(FACTORY.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(657780.87, result.getCoordinate().getX(), 0.1);
        assertEquals(6860728.96, result.getCoordinate().getY(), 0.1);

        // Combine it with a translation
        result = new Converter(ATID, crsTransform, AffineTransformation.translationInstance(-657780.87, -6860728.96))
            .convert(FACTORY.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(0.0, result.getCoordinate().getX(), 0.1);
        assertEquals(0.0, result.getCoordinate().getY(), 0.1);

        // Combine with translation & rotation (rotation center should be given point)
        result = new Converter(ATID, crsTransform, AffineTransformation.translationInstance(-657780.87, -6860728.96).rotate(1.0))
            .convert(FACTORY.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(0.0, result.getCoordinate().getX(), 0.1);
        assertEquals(0.0, result.getCoordinate().getY(), 0.1);

        // Pre and post transformations
        result = new Converter(
            AffineTransformation.translationInstance(48.8452222, 2.4247222),
            crsTransform,
            AffineTransformation.translationInstance(-657780.87, -6860728.96))
            .convert(FACTORY.createPoint(new Coordinate(0, 0)));

        assertEquals(0.0, result.getCoordinate().getX(), 0.1);
        assertEquals(0.0, result.getCoordinate().getY(), 0.1);
    }

    @Test
    @DisplayName("Testing reverse conversions")
    public void testReverseConversions() throws TransformException, FactoryException {
        MapCoordinates coords;

        Converter converter;

        converter = new Converter(
            AffineTransformation.translationInstance(0.01, -0.02),
            CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:2154")),
            AffineTransformation.translationInstance(-657780.0, -6860730.0).rotate(-3.0));

        Converter inverse = converter.inverse();

        coords = converter.convert(new MapCoordinates(48.0, 2.0));

        assertEquals(20441.0, coords.x(), 1.0);
        assertEquals(96352.0, coords.y(), 1.0);

        coords = inverse.convert(coords);

        assertEquals(48.0, coords.x(), 0.001); // We expect a 1m precision ~ 0,001 degree
        assertEquals(2.0, coords.y(), 0.001);
    }

    @Test
    @DisplayName("Testing prepend transformation on a converter")
    public void testPrependTransformation() throws TransformException, FactoryException {
        Converter converter = new Converter(
            ATID,
            CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:2154")),
            AffineTransformation.translationInstance(-657780, -6860728)
        );

        converter = new Converter(AffineTransformation.translationInstance(48.8452222, 0.0), converter);
        converter = new Converter(AffineTransformation.translationInstance(0.0, 2.4247222), converter);

        MapCoordinates coords = converter.convert(new MapCoordinates(0.0, 0.0));
        assertEquals(0.0, coords.x(), 1.0);
        assertEquals(0.0, coords.y(), 1.0);
    }
}

