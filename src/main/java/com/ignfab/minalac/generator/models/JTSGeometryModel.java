package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polyline2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape3d.Point3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polygon3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polyline3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Model represented by a JTS Geometry.
 * It is voxelizable both in 2d and 3d.
 */
public class JTSGeometryModel extends Model implements ShapesVoxelizable2d, ShapesVoxelizable3d {
    private final Geometry geom;

    /**
     * Creates a new {@link JTSGeometryModel}.
     *
     * @param geom A JTS Geometry
     * @param converter Converter from geometry CRS to world coordinates
     */
    public JTSGeometryModel(Geometry geom, MapToWorldConverter converter) throws TransformException {
        super();
        // Until there is no need of it we don't keep original geometry.
        // Geometry is stored transformed into world coordinates
        this.geom = converter.convert(geom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShapesVoxelizer2d voxelize2d(WorldBBox2d bbox) {
        if (!computeGeometryBBox().intersects(bbox))
            return ShapesVoxelizer2d.EMPTY;
        ShapesVoxelizer2d voxelizer = new ShapesVoxelizer2d(bbox);
        convert(geom, new GeometryConverter2d(voxelizer));
        return voxelizer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShapesVoxelizer3d voxelize3d(WorldBBox3d bbox) {
        if (!computeGeometryBBox().intersects(bbox.to2d()))
            return ShapesVoxelizer3d.EMPTY;
        ShapesVoxelizer3d voxelizer = new ShapesVoxelizer3d(bbox);
        convert(geom, new GeometryConverter3d(voxelizer));
        return voxelizer;
    }

    private WorldBBox2d computeGeometryBBox() {
        // Compute bounding box
        Envelope envelope = geom.getEnvelopeInternal();

        return new WorldBBox2d(
            WorldCoords2d.floor(envelope.getMinX(), envelope.getMinY()),
            WorldCoords2d.ceil(envelope.getMaxX(), envelope.getMaxY())
        );
    }

    /**
     * Recursively converts geometry using the provided converter.
     * @param geometry the {@link Geometry} to treat
     * @param converter the {@link GeometryConverter} object to convert geometries
     */
    private void convert(Geometry geometry, GeometryConverter converter) {
        switch (geometry.getGeometryType()) {
            // Single point
            case Geometry.TYPENAME_POINT -> converter.convertPoint((Point) geometry);
            // Lines
            case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING -> converter.convertLine((LineString) geometry);
            // Polygon (with holes)
            case Geometry.TYPENAME_POLYGON -> converter.convertPolygon((Polygon) geometry);
            // Geometry collections
            case Geometry.TYPENAME_MULTIPOINT, Geometry.TYPENAME_MULTILINESTRING, Geometry.TYPENAME_MULTIPOLYGON, Geometry.TYPENAME_GEOMETRYCOLLECTION -> {
                GeometryCollection collection = (GeometryCollection) geometry;
                for (int n = 0; n < collection.getNumGeometries(); n++)
                    convert(collection.getGeometryN(n), converter);
            }
            default -> System.err.println("Unable to treat geometry: " + geometry);
        }
    }

    /**
     * Tool able to convert JTS geometries into something else.
     */
    private interface GeometryConverter {
        /**
         * Converts a single point.
         * @param point the point to convert.
         */
        void convertPoint(Point point);

        /**
         * Converts a line string (multiple lines connected with each other).
         * It may be a basic line string (open) or a linear ring (end connected with start).
         * @param line the line to convert.
         */
        void convertLine(LineString line);

        /**
         * Converts a single polygon.
         * It may contain holes.
         * @param polygon the polygon to convert.
         */
        void convertPolygon(Polygon polygon);
    }

    /**
     * Implementation converting JTS geometries into 2d shapes.
     * Those shapes are stored in the given shapes voxelizer.
     * @param voxelizer the voxelizer to store converted shapes.
     */
    private record GeometryConverter2d(ShapesVoxelizer2d voxelizer) implements GeometryConverter {
        private WorldCoords2d convertCoordinate(Coordinate coordinate) {
            return WorldCoords2d.round(coordinate.x, coordinate.y);
        }

        private Polyline2d convertLineString(LineString line) {
            Coordinate[] coordinates = line.getCoordinates();
            List<WorldCoords2d> coords = new ArrayList<>(coordinates.length);
            for (Coordinate coordinate : coordinates)
                coords.add(convertCoordinate(coordinate));
            return Polyline2d.fromPoints(coords);
        }

        @Override
        public void convertPoint(Point point) {
            voxelizer.addShape(new Point2d(convertCoordinate(point.getCoordinate())));
        }

        @Override
        public void convertLine(LineString line) {
            voxelizer.addShape(convertLineString(line));
        }

        @Override
        public void convertPolygon(Polygon polygon) {
            Polyline2d shell = convertLineString(polygon.getExteriorRing());
            List<Polyline2d> holes = new ArrayList<>(polygon.getNumInteriorRing());
            for (int n = 0; n < polygon.getNumInteriorRing(); n++)
                holes.add(convertLineString(polygon.getInteriorRingN(n)));
            voxelizer.addShape(new Polygon2d(shell, holes));
        }
    }

    /**
     * Implementation converting JTS geometries into 3d shapes.
     * Those shapes are stored in the given shapes voxelizer.
     * @param voxelizer the voxelizer to store converted shapes.
     */
    private record GeometryConverter3d(ShapesVoxelizer3d voxelizer) implements GeometryConverter {
        private WorldCoords3d convertCoordinate(Coordinate coordinate) {
            return WorldCoords3d.round(coordinate.x, coordinate.y, coordinate.z);
        }

        private Polyline3d convertLineString(LineString line) {
            Coordinate[] coordinates = line.getCoordinates();
            List<WorldCoords3d> coords = new ArrayList<>(coordinates.length);
            for (Coordinate coordinate : coordinates)
                coords.add(convertCoordinate(coordinate));
            return Polyline3d.fromPoints(coords);
        }

        @Override
        public void convertPoint(Point point) {
            voxelizer.addShape(new Point3d(convertCoordinate(point.getCoordinate())));
        }

        @Override
        public void convertLine(LineString line) {
            voxelizer.addShape(convertLineString(line));
        }

        @Override
        public void convertPolygon(Polygon polygon) {
            Polyline3d shell = convertLineString(polygon.getExteriorRing());
            List<Polyline3d> holes = new ArrayList<>(polygon.getNumInteriorRing());
            for (int n = 0; n < polygon.getNumInteriorRing(); n++)
                holes.add(convertLineString(polygon.getInteriorRingN(n)));
            voxelizer.addShape(new Polygon3d(shell, holes));
        }
    }
}
