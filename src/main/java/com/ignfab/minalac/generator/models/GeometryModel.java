package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.EmptyChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;

import org.geotools.api.referencing.operation.TransformException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Model represented by a JTS Geometry.
 * It is rasterizable. Rasterized chunk will include three values:
 * 0: Not in geometry, 1: On its edge, 2: Inside it (polygons only).
 */
public class GeometryModel extends Model implements Rasterizable {
    private final Geometry geom;
    private IterableChunk2d chunk;
    private WorldBBox2d limits;

    /**
     * Outside the geometry.
     */
    public static final int OUTSIDE = 0;
    /**
     * On the border of the geometry.
     */
    public static final int BORDER = 1;
    /**
     * Inside the geometry.
     */
    public static final int INSIDE = 2;

    private static final Color OUTSIDE_COLOR = BufferedImageChunk.colorFor(OUTSIDE); // Not in shape color
    private static final Color BORDER_COLOR = BufferedImageChunk.colorFor(BORDER); // Shape border color
    private static final Color INSIDE_COLOR = BufferedImageChunk.colorFor(INSIDE); // Inside shape (fill) color

    /**
     * Constructs a new {@code GeometryModel}.
     *
     * @param geom A JTS Geometry
     * @param converter Converter from geometry CRS to world coordinates
     * @param limits A bounding box of rendering limits
     */
    public GeometryModel(Geometry geom, CoordsConverter converter, WorldBBox2d limits) throws TransformException {
        super();
        // Until there is no need of it we don't keep original geometry.
        // Geometry is stored transformed into world coordinates
        this.geom = converter.convert(geom);
        this.limits = limits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableChunk2d getChunk() {

        if (chunk == null) {
            makeChunk();
        }

        return chunk;
    }

    /**
     * Creates chunk, performing rasterization.
     * This could be made public if really needed.
     */
    private void makeChunk() {
        // Compute bounding box
        Envelope envelope = geom.getEnvelopeInternal();

        WorldBBox2d bbox = new WorldBBox2d(
            new WorldCoords2d((int) Math.floor(envelope.getMinX()), (int) Math.floor(envelope.getMinY())),
            new WorldCoords2d((int) Math.floor(envelope.getMaxX()), (int) Math.floor(envelope.getMaxY())));

        if (limits != null)
            bbox = bbox.intersection(limits);

        if (bbox.isEmpty()) {
            chunk = EmptyChunk2d.getInstance();
            return;
        }

        // Create chunk
        BufferedImageChunk chunk = new BufferedImageChunk(bbox);
        this.chunk = chunk;
        Graphics2D graphics = chunk.createGraphics();

        // Draw geometry translated into bounding box relative coordinates (i.e. BufferedImage coordinates)
        Geometry geom = AffineTransformation.translationInstance(-bbox.getMinX(), -bbox.getMinY()).transform(this.geom);
        draw(graphics, geom);

        graphics.dispose();
    }

    /**
     * Recursively draw geometry on buffered image.
     * @param graphics the {@link Graphics2D} object to draw on
     * @param geometry the {@link Geometry} to draw
     */
    private void draw(Graphics2D graphics, Geometry geometry) {
        ConvertedCoords coords;

        switch (geometry.getGeometryType()) {

            // Simple geometries
            case Geometry.TYPENAME_POINT:
            case Geometry.TYPENAME_LINESTRING:
            case Geometry.TYPENAME_LINEARRING:
                coords = new ConvertedCoords(geometry.getCoordinates());
                graphics.setColor(BORDER_COLOR);
                switch (geometry.getGeometryType()) {
                    case Geometry.TYPENAME_POINT:
                        graphics.drawRect(coords.x[0], coords.y[0], 1, 1);
                        break;
                    case Geometry.TYPENAME_LINESTRING:
                        graphics.drawPolyline(coords.x, coords.y, coords.length);
                        break;
                    case Geometry.TYPENAME_LINEARRING:
                        graphics.drawPolygon(coords.x, coords.y, coords.length);
                        break;
                }
                break;

            // Geometry collections
            case Geometry.TYPENAME_GEOMETRYCOLLECTION:
            case Geometry.TYPENAME_MULTILINESTRING:
            case Geometry.TYPENAME_MULTIPOINT:
            case Geometry.TYPENAME_MULTIPOLYGON:
                GeometryCollection collection = (GeometryCollection) geometry;
                for (int n = 0; n < collection.getNumGeometries(); n++)
                    draw(graphics, collection.getGeometryN(n));
                break;

            // Polygons (with holes)
            case Geometry.TYPENAME_POLYGON:
                Polygon polygon = (Polygon) geometry;
                coords = new ConvertedCoords(polygon.getExteriorRing().getCoordinates());

                graphics.setColor(INSIDE_COLOR);
                graphics.fillPolygon(coords.x, coords.y, coords.length);
                graphics.setColor(BORDER_COLOR);
                graphics.drawPolygon(coords.x, coords.y, coords.length);

                if (polygon.getNumInteriorRing() > 0) {
                    for (int n = 0; n < polygon.getNumInteriorRing(); n++) {
                        coords = new ConvertedCoords(polygon.getInteriorRingN(n).getCoordinates());

                        graphics.setColor(OUTSIDE_COLOR); // Wipe inside hole
                        graphics.fillPolygon(coords.x, coords.y, coords.length);
                        graphics.setColor(BORDER_COLOR);
                        graphics.drawPolygon(coords.x, coords.y, coords.length);
                    }
                }
                break;
        }
    }

    // This class converts float coord array into two integer arrays ready for Graphics2D
    private static class ConvertedCoords {
        private final int[] x;
        private final int[] y;
        private final int length;

        ConvertedCoords(Coordinate[] coords) {
            length = coords.length;
            x = new int[length];
            y = new int[length];

            for (int n = 0; n < length; n++) {
                x[n] = (int) Math.floor(coords[n].x);
                y[n] = (int) Math.floor(coords[n].y);
            }
        }
    }
}
