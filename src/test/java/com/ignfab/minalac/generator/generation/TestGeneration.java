package com.ignfab.minalac.generator.generation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class TestGeneration {
    @Test
    public void testGeneration() throws FactoryException, TransformException {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:2154");
        Generation generation = new Generation(crs, 601000.0, 6341000.0, 501, 501, 2.0, 3.0);

        WorldBBox2d box = generation.getWorldBBox2d();
        assertEquals(-250, box.getMinX());
        assertEquals(-250, box.getMinY());
        assertEquals(250, box.getMaxX());
        assertEquals(250, box.getMaxY());

        // We should have an envelope +/- 500 around center (250 voxel * 2.0 meters/voxels = 500m)
        Envelope envelope = generation.getEnvelopeForCRS(crs);
        assertEquals(600500.0, envelope.getMinX(), 0.01);
        assertEquals(6340500.0, envelope.getMinY(), 0.01);
        assertEquals(601500.0, envelope.getMaxX(), 0.01);
        assertEquals(6341500.0, envelope.getMaxY(), 0.01);

        // Try another CRS
        envelope = generation.getEnvelopeForCRS(CRS.decode("EPSG:4326"));
        assertEquals(44.1564, envelope.getMinX(), 0.0002);
        assertEquals(1.7559, envelope.getMinY(), 0.0002);

        // Coords converter from WSG84
        CoordsConverter converter = generation.makeCoordsConverter(CRS.decode("EPSG:4326"));
        // 44.1655934, 1.7682873 is WSG84 for 601500, 6341500 which correspond to voxel 250,250.
        Geometry geometry = new GeometryFactory().createPoint(new Coordinate(44.1655934, 1.7682873));
        geometry = converter.convert(geometry);
        assertEquals(250.0, geometry.getCoordinate().x, 0.01);
        assertEquals(250.0, geometry.getCoordinate().y, 0.01);
    }
}
