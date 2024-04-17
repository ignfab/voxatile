package com.ignfab.minalac.generator.models;

import java.awt.Graphics2D;
import java.awt.Color;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

import org.geotools.api.referencing.operation.TransformException;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

// This class converts float coord array into two integer arrays ready for Graphics2D
class ConvertedCoords {
    public int[] x;
    public int[] y;
    public int length;

    ConvertedCoords(Coordinate[] coords) {
        length = coords.length;
        x = new int[length];
        y = new int[length];

        for (int n = 0; n < length; n++) {
            x[n] = (int)Math.floor(coords[n].x);
            y[n] = (int)Math.floor(coords[n].y);
        }
    }
}

/**
 * Model represented by a JTS Geometry
 *
 * It is rasterizable. Rasterized chunk will include three values:
 * 0: Not in geometry, 1: On its edge, 2: Inside it (polygons only).
 */
public class GeometryModel implements Rasterizable {
    private Geometry geom;
    private BufferedImageChunk chunk;

    public static final int OUTSIDE = 0;
    public static final int BORDER = 1;
    public static final int INSIDE = 2;

    private static final Color outsideColor = BufferedImageChunk.colorFor(OUTSIDE); // Not in shape color
    private static final Color borderColor = BufferedImageChunk.colorFor(BORDER); // Shape border color
    private static final Color insideColor = BufferedImageChunk.colorFor(INSIDE); // Inside shape (fill) color



    /**
     * Constructs a new {@code GeometryModel}
     *
     * @param geom A JTS Geometry
     * @param converter Converter from geometry CRS to world coordinates
     */
    public GeometryModel(Geometry geom, CoordsConverter converter) throws TransformException{
        // Until there is no need of it we don't keep original geometry.
        // Geometry is stored transformed into world coordinates
        this.geom = converter.convert(geom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImageChunk getChunk() {

        if (chunk == null) {
            makeChunk();
        }

        return chunk;
    };

    /**
     * Creates chunk, performing rasterization.
     *
     * This could be made public if really needed.
     */
    private void makeChunk() {
        // Compute bounding box
        Envelope envelope = geom.getEnvelopeInternal();

        WorldBBox2d bbox = new WorldBBox2d(
            new WorldCoords2d((int)Math.floor(envelope.getMinX()), (int)Math.floor(envelope.getMinY())),
            new WorldCoords2d((int)Math.floor(envelope.getMaxX()), (int)Math.floor(envelope.getMaxY())));

        // Create chunk
        chunk = new BufferedImageChunk(bbox);
        Graphics2D graphics = chunk.createGraphics();

        // Draw geometry translated into bounding box relative coordinates (i.e. BufferedImage coordinates))
        Geometry geom = AffineTransformation.translationInstance(-Math.floor(envelope.getMinX()), -Math.floor(envelope.getMinY())).transform(this.geom);
        draw(graphics, geom);

        graphics.dispose();
    }

    /**
     * Recursively draw geometry on buffered image
     */
    private void draw(Graphics2D graphics, Geometry geometry) {
        ConvertedCoords coords;

        switch (geometry.getGeometryType()) {

            // Simple geometries
            case Geometry.TYPENAME_POINT:
            case Geometry.TYPENAME_LINESTRING:
            case Geometry.TYPENAME_LINEARRING:
                coords = new ConvertedCoords(geometry.getCoordinates());
                graphics.setColor(borderColor);
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
                GeometryCollection collection = (GeometryCollection)geometry;
                for (int n = 0; n < collection.getNumGeometries(); n++)
                    draw(graphics, collection.getGeometryN(n));
                break;

            // Polygons (with holes)
            case Geometry.TYPENAME_POLYGON:
                Polygon polygon = (Polygon)geometry;
                coords = new ConvertedCoords(polygon.getExteriorRing().getCoordinates());

                graphics.setColor(insideColor);
                graphics.fillPolygon(coords.x, coords.y, coords.length);
                graphics.setColor(borderColor);
                graphics.drawPolygon(coords.x, coords.y, coords.length);

                if (polygon.getNumInteriorRing() > 0) {
                    for (int n = 0; n < polygon.getNumInteriorRing(); n++) {
                        coords = new ConvertedCoords(polygon.getInteriorRingN(n).getCoordinates());

                        graphics.setColor(outsideColor); // Wipe inside hole
                        graphics.fillPolygon(coords.x, coords.y, coords.length);
                        graphics.setColor(borderColor);
                        graphics.drawPolygon(coords.x, coords.y, coords.length);
                    }
                }
                break;
        }
    }
}

