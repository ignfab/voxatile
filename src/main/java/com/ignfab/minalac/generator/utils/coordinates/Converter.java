package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.NoninvertibleTransformException;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.util.NoninvertibleTransformationException;

import com.ignfab.minalac.generator.exceptions.TransformException;

/**
 * Converts coordinates from a CRS to world coordinates.
 */
public class Converter {
    private final AffineTransformation preTransform;
    private final MathTransform crsTransform;
    private final AffineTransformation postTransform;

    /**
     * Creates a new {@code CoordsConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usually for scale and rotation).
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    protected Converter(AffineTransformation preTransform, MathTransform crsTransform, AffineTransformation postTransform) {
        if (preTransform == null || crsTransform == null || postTransform == null)
            throw new IllegalArgumentException("No transformation should be null");

        if (crsTransform.getSourceDimensions() != 2 || crsTransform.getTargetDimensions() != 2)
            throw new IllegalArgumentException("crsTransform must be from two dimensions to two dimensions");

        this.preTransform = preTransform;
        this.crsTransform = crsTransform;
        this.postTransform = postTransform;
    }

    /**
     * Constructs a new {@code CoordsConverter} from another, adding a preTransformation.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param converter Converter to start from
     */
    protected Converter(AffineTransformation preTransform, Converter converter) {
        this(preTransform.composeBefore(converter.preTransform), converter.crsTransform, converter.postTransform);
    }

    /**
     * Creates a new {@code CoordinatesConverter} performing inverse transformation.
     *
     * @return inverse coverter
     * @throws TransformException if coverter cannot be inverted
     */
    protected Converter inverse() throws TransformException {
        try {
            return new Converter(postTransform.getInverse(), crsTransform.inverse(), preTransform.getInverse());
        } catch (NoninvertibleTransformationException | NoninvertibleTransformException e) {
            throw new TransformException("Converter is not invertible", e);
        }
    }

    /**
     * Converts coordinates.
     *
     * @param coords Coordinates to convert
     *
     * @return converted coordiantes
     *
     * @throws TransformException if conversion cannot be performed
     */
    public MapCoordinates convert(MapCoordinates coords) throws TransformException {
        Coordinate coordinate = new Coordinate();
        try {

            preTransform.transform(new CoordinateXY(coords.x(), coords.y()), coordinate);
            Position position = crsTransform.transform(new Position2D(coordinate.x, coordinate.y), null);
            postTransform.transform(new Coordinate(position.getOrdinate(0), position.getOrdinate(1)), coordinate);
            return new MapCoordinates(coordinate.getX(), coordinate.getY());
        } catch (MismatchedDimensionException e) {
            // This should never occur as we check dimensions in constructor
            throw new TransformException("Dimension mismatch",  e);
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform coordinates",  e);
        }
    }

    /**
     * Converts a JTS geometry.
     *
     * @param geom Geometry to transform.
     * @return Transformed geometry.
     * @throws TransformException if unable to perform transformation.
     */
    public Geometry convert(Geometry geom) throws TransformException {
        try {
            return postTransform.transform(JTS.transform(preTransform.transform(geom), crsTransform));
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform geometry", e);
        }
    }
}
