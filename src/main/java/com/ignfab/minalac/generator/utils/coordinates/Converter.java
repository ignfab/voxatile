package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.NoninvertibleTransformException;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
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
    private final double altitudeOffset;
    private final double altitudeScale;

    /**
     * Creates a new {@code CoordsConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usually for scale and rotation).
     * @param altitudeOffset Offset in map units (likely metters) to add to real altitude before conversion.
     * @param altitudeScale Scale to apply to altitude after offset in voxels per map units.
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    protected Converter(
        AffineTransformation preTransform,
        MathTransform crsTransform,
        AffineTransformation postTransform,
        double altitudeOffset,
        double altitudeScale
    ) {
        if (preTransform == null || crsTransform == null || postTransform == null)
            throw new IllegalArgumentException("No transformation should be null");

        if (crsTransform.getSourceDimensions() != 2 || crsTransform.getTargetDimensions() != 2)
            throw new IllegalArgumentException("crsTransform must be from two dimensions to two dimensions");

        this.preTransform = preTransform;
        this.crsTransform = crsTransform;
        this.postTransform = postTransform;
        this.altitudeOffset = altitudeOffset;
        this.altitudeScale = altitudeScale;
    }

    /**
     * Constructs a new {@code CoordsConverter} from another, adding a preTransformation.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param converter Converter to start from
     */
    protected Converter(AffineTransformation preTransform, Converter converter) {
        this(
            preTransform.composeBefore(converter.preTransform),
            converter.crsTransform,
            converter.postTransform,
            converter.altitudeOffset,
            converter.altitudeScale
        );
    }

    /**
     * Creates a new {@code CoordinatesConverter} performing inverse transformation.
     *
     * @return inverse coverter
     * @throws TransformException if coverter cannot be inverted
     */
    protected Converter inverse() throws TransformException {
        if (altitudeScale == 0.0)
            throw new TransformException("Converter is not invertible because of altitude scale");
        try {
            return new Converter(
                postTransform.getInverse(),
                crsTransform.inverse(),
                preTransform.getInverse(),
                -altitudeOffset * altitudeScale, // TODO: A REVOIR
                1.0 / altitudeScale);
        } catch (NoninvertibleTransformationException | NoninvertibleTransformException e) {
            throw new TransformException("Converter is not invertible because of map transformation", e);
        }
    }
    /**
     * Converts atlitude.
     *
     * @param altitude Altitude to convert
     *
     * @return converted altitude
     */
    public double convertAltitude(double altitude) {
        return (altitude + altitudeOffset) * altitudeScale;
    }

    /**
     * Converts 2d coordinates.
     *
     * @param coords Coordinates to convert
     *
     * @return converted coordiantes
     *
     * @throws TransformException if conversion cannot be performed
     */
    public MapCoordinates2d convert(MapCoordinates2d coords) throws TransformException {
        Coordinate coordinate = new Coordinate();
        try {
            preTransform.transform(new CoordinateXY(coords.x(), coords.y()), coordinate);
            Position position = crsTransform.transform(new Position2D(coordinate.x, coordinate.y), null);
            postTransform.transform(new Coordinate(position.getOrdinate(0), position.getOrdinate(1)), coordinate);
            return new MapCoordinates2d(coordinate.getX(), coordinate.getY());
        } catch (MismatchedDimensionException e) {
            // This should never occur as we check dimensions in constructor
            throw new TransformException("Dimension mismatch",  e);
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform coordinates",  e);
        }
    }

    /**
     * Converts 3d coordinates.
     *
     * @param coords Coordinates to convert
     *
     * @return converted coordiantes
     *
     * @throws TransformException if conversion cannot be performed
     */
    public MapCoordinates3d convert(MapCoordinates3d coords) throws TransformException {
        Coordinate coordinate = new Coordinate();
        try {
            preTransform.transform(new CoordinateXY(coords.x(), coords.y()), coordinate);
            Position position = crsTransform.transform(new Position2D(coordinate.x, coordinate.y), null);
            postTransform.transform(new Coordinate(position.getOrdinate(0), position.getOrdinate(1)), coordinate);
            return new MapCoordinates3d(coordinate.getX(), coordinate.getY(), convertAltitude(coords.z()));
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

        Geometry g = geom.copy();
        g.apply(new AltitudeFilter());

        try {
            return postTransform.transform(JTS.transform(preTransform.transform(g), crsTransform));
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform geometry", e);
        }
    }

    private final class AltitudeFilter implements CoordinateFilter {

        @Override
        public void filter(Coordinate coord) {
            coord.z = convertAltitude(coord.z);
        }
    }
}
