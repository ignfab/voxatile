package com.ignfab.minalac.generator.generation;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

import static org.junit.jupiter.api.Assertions.*;

public class TestGeneration {
    private static CoordinateReferenceSystem crs2154;
    private static CoordinateReferenceSystem crs4326;
    private static CoordinateReferenceSystem crs3857;
    private static Seed seed;

    @BeforeAll
    public static void setUp() throws FactoryException {
        crs2154 = CRS.decode("EPSG:2154");
        crs4326 = CRS.decode("EPSG:4326");
        crs3857 = CRS.decode("EPSG:3857");
        seed = new Seed("ABCD");
    }

    // 657_781, 6_860_729 (EPSG:2154) IGN Saint Mandé
    private final int x2154 = 657_781;
    private final int y2154 = 6_860_729;

    @Test
    public void testGeneration() throws FactoryException, TransformException {
        VoxelWorld world = new EmptyVoxelWorld(500, 500);

        Generation generation = new Generation(world, seed, crs2154, x2154, y2154, 500, 500, 2.0, 3.0, 0.5 * Math.PI);

        assertEquals(seed, generation.seed());
        WorldBBox3d box = generation.world().limits();
        assertEquals(-250, box.minX());
        assertEquals(-250, box.minY());
        assertEquals(249, box.maxX());
        assertEquals(249, box.maxY());

        // We should have an envelope +/- 500 around center (250 voxel * 2.0 meters/voxels = 500m)
        Envelope envelope = generation.getEnvelopeForCRS(crs2154, box);
        assertEquals(657_280.0, envelope.getMinX(), 2);
        assertEquals(6_860_230.0, envelope.getMinY(), 2);
        assertEquals(658_280.0, envelope.getMaxX(), 2);
        assertEquals(6_861_230.0, envelope.getMaxY(), 2);

        // Try another CRS
        envelope = generation.getEnvelopeForCRS(crs4326, box);
        assertEquals(48.8407, envelope.getMinX(), 0.0002);
        assertEquals(2.4179, envelope.getMinY(), 0.0002);

        MapToWorldConverter converter;
        Geometry geometry;

        // Check rotation
        converter = generation.makeCoordsConverter(crs2154);
        geometry = new GeometryFactory().createPoint(new Coordinate(x2154 + 10, y2154 + 20));
        geometry = converter.convert(geometry);
        assertEquals(10.0, geometry.getCoordinate().x, 0.01); // (We have a 2m per voxel scale factor)
        assertEquals(-5.0, geometry.getCoordinate().y, 0.01);

        // Coords converter from WSG84
        converter = generation.makeCoordsConverter(crs3857);
        // 269_919.0354, 6_248_639.6317 is EPSG:3857 for 657_781, 6_860_729 which correspond to voxel 0,0.
        geometry = new GeometryFactory().createPoint(new Coordinate(269_919.0354, 6_248_639.6317));
        geometry = converter.convert(geometry);
        assertEquals(0.0, geometry.getCoordinate().x, 0.01);
        assertEquals(0.0, geometry.getCoordinate().y, 0.01);
    }

    private static class EmptyVoxelWorld extends VoxelWorld {
        private int extentX;
        private int extentY;

        protected EmptyVoxelWorld(int extentX, int extentY) {
            super(new VoxelWorldMetadata());
            this.extentX = extentX;
            this.extentY = extentY;
        }

        @Override
        public WorldBBox3d maxLimits() {
            return new WorldBBox3d(-extentX / 2, -extentY / 2, 0, extentX, extentY, 1);
        }

        @Override
        public void initialize() throws MapWriteException {
        }

        @Override
        public void finalizeAndSave() throws MapWriteException {
        }

        @Override
        public VoxelTile newTile(WorldBBox3d limits) {
            throw new UnsupportedOperationException("Unimplemented method 'newTile'");
        }
    }
}
