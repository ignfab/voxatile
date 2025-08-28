package com.ignfab.minalac.generator.models;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.MultiShape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LinearRing3d;
import com.ignfab.minalac.generator.voxelization.shape3d.MultiShape3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Point3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polygon3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

/**
 * Model represented by a JTS Geometry.
 * It is convertible to both Shape2d and Shape3d.
 */
public class JTSGeometryModel extends ModelImpl implements Shape2dConvertibleModel, Shape3dConvertibleModel {
    private Geometry geom;

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
     * Gets the model's geometry.
     * The returned object is a direct reference and any modification
     * will be reflected in the model, until another geometry is set.
     * <p>
     * Note: The current geometry is different from the one used in the
     * constructor, because it was converted using the given converter.
     * @return The current geometry
     * @see #setGeometry(Geometry)
     */
    public Geometry getGeometry() {
        return geom;
    }

    /**
     * Sets the model's geometry.
     * This will overwrite the current geometry of the model, and therefore
     * the new geometry should probably be computed from the current one.
     * <p>
     * Note: The new geometry won't be converted again like the one used in
     * the constructor. It must be in the world's coordinate system.
     * @param geom The new geometry
     */
    public void setGeometry(Geometry geom) {
        this.geom = geom;
    }

    @Override
    public String salt() {
        Point p = this.geom.getCentroid();
        if (p == null)
            // Maybe we should rely on something else in that case (metadata?)?
            return "";

        // This should ensure enough uniqueness
        return p.getX() + "/" + p.getY();
    }

    @Override
    public Shape3d toShape3d() {
        GeometryConverter3d converter = new GeometryConverter3d();
        convert(geom, converter);
        return converter.result();
    }


    @Override
    public Shape2d toShape2d() {
        GeometryConverter2d converter = new GeometryConverter2d();
        convert(geom, converter);
        return converter.result();
    }

    /**
     * Recursively converts geometry using the provided converter.
     * @param geometry the {@link Geometry} to treat
     * @param converter the {@link GeometryConverter} object to convert geometries
     */
    private void convert(Geometry geometry, GeometryConverter converter) {
        switch (geometry.getGeometryType()) {
            // Single point
            case Geometry.TYPENAME_POINT -> converter.convert((Point) geometry);
            // Line strings
            case Geometry.TYPENAME_LINESTRING -> converter.convert((LineString) geometry);
            // Linear ring
            case Geometry.TYPENAME_LINEARRING -> converter.convert((LinearRing) geometry);
            // Polygon (with holes)
            case Geometry.TYPENAME_POLYGON -> converter.convert((Polygon) geometry);
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
        void convert(Point point);

        /**
         * Converts a line string (multiple lines connected with each other).
         * @param lineString the line string to convert.
         */
        void convert(LineString lineString);

        /**
         * Converts a linear ring (a line string forming a ring).
         * @param linearRing the linear ring to convert.
         */
        void convert(LinearRing linearRing);

        /**
         * Converts a single polygon.
         * It may contain holes.
         * @param polygon the polygon to convert.
         */
        void convert(Polygon polygon);
    }

    /**
     * Implementation converting JTS geometries into 2d shapes.
     */
    private final class GeometryConverter2d implements GeometryConverter {

        private Shape2d result;

        private Shape2d result() {
            return result;
        }

        private void addShape(Shape2d shape) {
            if (result == null)
                result = shape;
            else
                if (result instanceof MultiShape2d collection)
                    collection.addShape(shape);
                else
                    result = new MultiShape2d(result, shape);
        }

        private WorldCoords2d convertCoordinate(Coordinate coordinate) {
            return WorldCoords2d.round(coordinate.x, coordinate.y);
        }

        private List<WorldCoords2d> convertCoordinates(LineString line) {
            Coordinate[] coordinates = line.getCoordinates();
            List<WorldCoords2d> coords = new ArrayList<>(coordinates.length);
            for (Coordinate coordinate : coordinates)
                coords.add(convertCoordinate(coordinate));
            return coords;
        }

        @Override
        public void convert(Point point) {
            addShape(new Point2d(convertCoordinate(point.getCoordinate())));
        }

        @Override
        public void convert(LineString line) {
            addShape(LineString2d.fromPoints(convertCoordinates(line)));
        }

        @Override
        public void convert(LinearRing ring) {
            addShape(LinearRing2d.fromPoints(convertCoordinates(ring)));
        }

        @Override
        public void convert(Polygon polygon) {
            LinearRing2d shell = LinearRing2d.fromPoints(convertCoordinates(polygon.getExteriorRing()));
            List<LinearRing2d> holes = new ArrayList<>(polygon.getNumInteriorRing());
            for (int n = 0; n < polygon.getNumInteriorRing(); n++)
                holes.add(LinearRing2d.fromPoints(convertCoordinates(polygon.getInteriorRingN(n))));
            addShape(new Polygon2d(shell, holes));
        }
    }

    /**
     * Implementation converting JTS geometries into 3d shapes.
     */
    private final class GeometryConverter3d implements GeometryConverter {

        private Shape3d result;

        private Shape3d result() {
            return result;
        }

        private void addShape(Shape3d shape) {
            if (result == null)
                result = shape;
            else
                if (result instanceof MultiShape3d collection)
                    collection.addShape(shape);
                else
                    result = new MultiShape3d(result, shape);
        }

        private WorldCoords3d convertCoordinate(Coordinate coordinate) {
            return WorldCoords3d.round(coordinate.x, coordinate.y, coordinate.z);
        }

        private List<WorldCoords3d> convertCoordinates(LineString line) {
            Coordinate[] coordinates = line.getCoordinates();
            List<WorldCoords3d> coords = new ArrayList<>(coordinates.length);
            for (Coordinate coordinate : coordinates)
                coords.add(convertCoordinate(coordinate));
            return coords;
        }

        @Override
        public void convert(Point point) {
            addShape(new Point3d(convertCoordinate(point.getCoordinate())));
        }

        @Override
        public void convert(LineString line) {
            addShape(LineString3d.fromPoints(convertCoordinates(line)));
        }

        @Override
        public void convert(LinearRing ring) {
            addShape(LinearRing3d.fromPoints(convertCoordinates(ring)));
        }

        @Override
        public void convert(Polygon polygon) {
            LinearRing3d shell = LinearRing3d.fromPoints(convertCoordinates(polygon.getExteriorRing()));
            List<LinearRing3d> holes = new ArrayList<>(polygon.getNumInteriorRing());
            for (int n = 0; n < polygon.getNumInteriorRing(); n++)
                holes.add(LinearRing3d.fromPoints(convertCoordinates(polygon.getInteriorRingN(n))));
            addShape(new Polygon3d(shell, holes));
        }
    }
}
