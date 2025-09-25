package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapToWorldConverterTest {

    @Test
    public void testConvertAffineTransformationOnly() throws FactoryException, TransformException {
        // Verify translation
        WorldCoords2d result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            10,
            -20,
            1.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates2d(-10, 20));
        assertEquals(0, result.x());
        assertEquals(0, result.y());

        // Verify 180° rotation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            Math.PI,
            0
        ).convert(new MapCoordinates2d(-1, 2));
        assertEquals(1, result.x());
        assertEquals(-2, result.y());

        // Verify 90° rotation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            Math.PI / 2.0,
            0
        ).convert(new MapCoordinates2d(1, -2));
        assertEquals(2, result.x());
        assertEquals(1, result.y());

        // Verify scale transformation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            2.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates2d(1.5, -5));
        assertEquals(3, result.x());
        assertEquals(-10, result.y());

        // verticalScale should not have any influence on 2d coordinates
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            10.0,
            0,
            0
        ).convert(new MapCoordinates2d(2, 3));
        assertEquals(2, result.x());
        assertEquals(3, result.y());
    }

    @Test
    @DisplayName("Conversion tests on 3D coordinates")
    public void testConvert3D() throws FactoryException, TransformException {
        // Verify translation
        WorldCoords3d result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            10,
            -20,
            1.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates3d(-10, 20, 7));
        assertEquals(0, result.x());
        assertEquals(0, result.y());
        assertEquals(7, result.z());

        // Verify 180° rotation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            Math.PI,
            0
        ).convert(new MapCoordinates3d(-1, 2, 7));
        assertEquals(1, result.x());
        assertEquals(-2, result.y());
        assertEquals(7, result.z());

        // Verify 90° rotation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            Math.PI / 2.0,
            0
        ).convert(new MapCoordinates3d(1, -2, 7));
        assertEquals(2, result.x());
        assertEquals(1, result.y());
        assertEquals(7, result.z());

        // Verify horizontal scaling transformation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            2.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates3d(1.5, -5, 7));
        assertEquals(3, result.x());
        assertEquals(-10, result.y());
        assertEquals(7, result.z());

        // Verify vertical scaling transformation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            10.0,
            0,
            0
        ).convert(new MapCoordinates3d(2, 3, 7.7));
        assertEquals(2, result.x());
        assertEquals(3, result.y());
        assertEquals(77, result.z());
    }

    @Test
    @DisplayName("Conversion tests on 3D coordinates")
    public void testConvertOnGeometry() throws TransformException, FactoryException {
        Geometry result;

        Coordinate[] coords = {new Coordinate(-2.0, -2.0), new Coordinate(-1.0, 2.0), new Coordinate(2.0, 0.0)};

        result = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            Math.PI / 2.0,
            0
        ).convert(new GeometryFactory().createLineString(coords));

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
    public void testConvertWithProjection() throws FactoryException, TransformException {
        // Check simple geographic conversion from WSG 84 to LAMBERT 93
        // IGN leveling mark https://geodesie.ign.fr/fiches/pdf/P.C.K3L3-20b_534424.pdf
        WorldCoords2d result = new MapToWorldConverter(
            CRS.decode("EPSG:4326"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates2d(48.8452222, 2.4247222));
        assertEquals(657_780, result.x());
        assertEquals(6_860_728, result.y());

        // Combine it with a translation
        result = new MapToWorldConverter(
            CRS.decode("EPSG:4326"),
            CRS.decode("EPSG:2154"),
            -657_780,
            -6_860_728,
            1.0,
            1.0,
            0,
            0
        ).convert(new MapCoordinates2d(48.8452222, 2.4247222));
        assertEquals(0, result.x());
        assertEquals(0, result.y());
    }

    @Test
    public void testInverse() throws FactoryException, TransformException {
        // With projection only
        MapToWorldConverter mapToWorld = new MapToWorldConverter(
            CRS.decode("EPSG:4326"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            0,
            0
        );
        WorldToMapConverter worldToMap = mapToWorld.inverse();

        WorldCoords2d gameCoords = mapToWorld.convert(new MapCoordinates2d(48.84, 2.42));
        MapCoordinates2d realCoords = worldToMap.convert(gameCoords);
        assertEquals(48.84, realCoords.x(), 0.05); // Due to round-down precision is lost
        assertEquals(2.42, realCoords.y(), 0.05);

        // With projection and affine transformation
        mapToWorld = new MapToWorldConverter(
            CRS.decode("EPSG:4326"),
            CRS.decode("EPSG:2154"),
            5_000,
            -2_300,
            1.5,
            0.5,
            30,
            0
        );
        worldToMap = mapToWorld.inverse();

        gameCoords = mapToWorld.convert(new MapCoordinates2d(48.84, 2.42));
        realCoords = worldToMap.convert(gameCoords);
        assertEquals(48.84, realCoords.x(), 0.05);
        assertEquals(2.42, realCoords.y(), 0.05);
    }

    @Test
    public void testConvertAltitude() throws FactoryException {
        // Identity: verticalScale = 1.0, altitudeOffset = 0.0
        assertEquals(5, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            0,
            0
        )).convertAltitude(5));

        // verticalScale = 2.0, altitudeOffset = 0.0
        assertEquals(10, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            2.0,
            0,
            0
        ).convertAltitude(5)));

        // verticalScale = 2.0, altitudeOffset = 0.5
        assertEquals(11, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            2.0,
            0,
            0.5
        )).convertAltitude(5));

        // verticalScale = 2.0, altitudeOffset = -0.5
        assertEquals(9, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            2.0,
            0,
            -0.5
        )).convertAltitude(5));
    }

    @Test
    public void testConvertHorizontalDistance() throws FactoryException {
        // Identity: horizontalScale = 1.0
        assertEquals(5, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            0,
            0
        )).convertHorizontalDistance(5));

        // horizontalScale = 2.0
        assertEquals(10, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            2.0,
            1.0,
            0,
            0
        )).convertHorizontalDistance(5));

        // horizontalScale = 0.5
        assertEquals(3, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            0.5,
            1.0,
            0,
            0
        )).convertHorizontalDistance(6));
    }

    @Test
    public void testConvertVerticalDistance() throws FactoryException {
        // Identity: verticalScale = 1.0
        assertEquals(5, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            1.0,
            0,
            0
        )).convertVerticalDistance(5));

        // verticalScale = 2.0
        assertEquals(10, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            2.0,
            0,
            0
        )).convertVerticalDistance(5));

        // verticalScale = 0.5
        assertEquals(3, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            0.5,
            0,
            0
        )).convertVerticalDistance(6));

        // altitudeOffset should not have any influence. verticalScale = 0.5 altitudeOffset = 100
        assertEquals(3, (new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            1.0,
            0.5,
            0,
            100
        )).convertVerticalDistance(6));
    }
}
