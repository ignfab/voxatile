package com.ignfab.minalac.generator.generation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.IdentityTransform;

import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;

import com.ignfab.minalac.generator.generation.CoordsConverter;

public class TestCoordsConverter {

    private static GeometryFactory factory = new GeometryFactory();

    // Tests right usage of affine transformation
    @Test
    public void testPointAffineTransform() throws TransformException {
        Geometry result;

        // Verify identity transformation
        result = new CoordsConverter(IdentityTransform.create(2), new AffineTransformation())
            .convert(factory.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(),  1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), -2.0, 0.0001);

        // Verify 180° rotation transformation
        result = new CoordsConverter(IdentityTransform.create(2), AffineTransformation.rotationInstance(Math.PI))
            .convert(factory.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), -1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(),  2.0, 0.0001);

        // Verify 90° rotation transformation
        result = new CoordsConverter(IdentityTransform.create(2), AffineTransformation.rotationInstance(Math.PI / 2))
            .convert(factory.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), 2.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), 1.0, 0.0001);

        // Verify translation transformation
        result = new CoordsConverter(IdentityTransform.create(2), AffineTransformation.translationInstance(-2.0, 1.0))
            .convert(factory.createPoint(new Coordinate(1.0, -2.0)));

        assertEquals(result.getCoordinate().getX(), -1.0, 0.0001);
        assertEquals(result.getCoordinate().getY(), -1.0, 0.0001);
    }

    // Tests transformation works as expected on multipoint shapes
    @Test
    public void testShapeAffineTransformation() throws TransformException {
        Geometry result;

        Coordinate[] coords = {new Coordinate(-2.0, -2.0), new Coordinate(-1.0, 2.0), new Coordinate(2.0, 0.0)};

        result = new CoordsConverter(IdentityTransform.create(2), AffineTransformation.rotationInstance(Math.PI / 2))
            .convert(factory.createLineString(coords));

        coords = result.getCoordinates();

        // We assume that coords order has not changed but this is not sure at all.
        assertEquals(coords[0].getX(),  2.0, 0.0001);
        assertEquals(coords[0].getY(), -2.0, 0.0001);
        assertEquals(coords[1].getX(), -2.0, 0.0001);
        assertEquals(coords[1].getY(), -1.0, 0.0001);
        assertEquals(coords[2].getX(),  0.0, 0.0001);
        assertEquals(coords[2].getY(),  2.0, 0.0001);
    }

    // Tests projection works as expected, combined or not with affine transformations
    @Test
    public void testProjection() throws TransformException, NoSuchAuthorityCodeException, FactoryException {
        Geometry result;

        // Check simple geographic conversion from WSG 84 to LAMBERT 93
        // IGN leveling mark https://geodesie.ign.fr/fiches/pdf/P.C.K3L3-20b_534424.pdf
        MathTransform crsTransform = CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:2154"));
        result = new CoordsConverter(crsTransform, new AffineTransformation())
            .convert(factory.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(result.getCoordinate().getX(),  657780.0, 2);
        assertEquals(result.getCoordinate().getY(), 6860730.0, 2);

        // Combine it with a translation
        result = new CoordsConverter(crsTransform, AffineTransformation.translationInstance(-657780.0, -6860730.0))
            .convert(factory.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(result.getCoordinate().getX(), 0.0, 2);
        assertEquals(result.getCoordinate().getY(), 0.0, 2);

        // Combine with translation & rotation (rotation center should be given point)
        result = new CoordsConverter(crsTransform, AffineTransformation.translationInstance(-657780.0, -6860730.0).rotate(1.0))
            .convert(factory.createPoint(new Coordinate(48.8452222, 2.4247222)));

        assertEquals(result.getCoordinate().getX(), 0.0, 2);
        assertEquals(result.getCoordinate().getY(), 0.0, 2);
    }
}

