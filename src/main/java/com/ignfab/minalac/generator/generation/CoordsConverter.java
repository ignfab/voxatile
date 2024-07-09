package com.ignfab.minalac.generator.generation;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.NoninvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.util.NoninvertibleTransformationException;

import com.ignfab.minalac.generator.utils.coordinates.Coords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Converts coordinates from a CRS to world coordinates.
 */
public class CoordsConverter {
    private final AffineTransformation preTransform;
    private final MathTransform crsTransform;
    private final AffineTransformation postTransform;
    private final AffineTransformation preTransformInverse;
    private final MathTransform crsTransformInverse;
    private final AffineTransformation postTransformInverse;
    private final boolean invertible;

    /**
     * Constructs a new {@code CoordsConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usually for scale and rotation).
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    public CoordsConverter(AffineTransformation preTransform, MathTransform crsTransform, AffineTransformation postTransform) {
        if (preTransform == null || crsTransform == null || postTransform == null)
            throw new IllegalArgumentException("No transformation should be null");

        if (crsTransform.getSourceDimensions() != 2 || crsTransform.getTargetDimensions() != 2)
            throw new IllegalArgumentException("crsTransform must be from two dimensions to two dimensions");

        this.preTransform = preTransform;
        this.crsTransform = crsTransform;
        this.postTransform = postTransform;

        // Try compute inverse transformations
        AffineTransformation preTransformInverse = null;
        MathTransform crsTransformInverse = null;
        AffineTransformation postTransformInverse = null;
        boolean invertible = false;

        try {
            preTransformInverse = preTransform.getInverse();
            crsTransformInverse = crsTransform.inverse();
            postTransformInverse = postTransform.getInverse();
            invertible = true;
        } catch (NoninvertibleTransformationException | NoninvertibleTransformException e) {
        }

        this.preTransformInverse = preTransformInverse;
        this.crsTransformInverse = crsTransformInverse;
        this.postTransformInverse = postTransformInverse;
        this.invertible = invertible;
    }

    /**
     * Constructs a new {@code CoordsConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usualy for scale and rotation).
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    public CoordsConverter(MathTransform crsTransform, AffineTransformation postTransform) {
        this(new AffineTransformation(), crsTransform, postTransform);
    }

    /**
     * Constructs a new {@code CoordsConverter} from another, adding a preTransformation.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param converter Converter to start from
     */
    public CoordsConverter(AffineTransformation preTransform, CoordsConverter converter) {
        this(preTransform.composeBefore(converter.preTransform), converter.crsTransform, converter.postTransform);
    }

    /**
     * Tells if {@code CoordsConverter} is invertible, which means {@code reverse} methods could be used.
     *
     * @return {@code true} if invertible
     */
    public boolean isInvertible() {
        return invertible;
    }

    /**
     * Converts map coordinates into world coordinates applying a factor.
     * This is useful to handle millivoxels for example (with factor 1000).
     *
     * @param coords Coordinates in map
     * @param factor Factor by which coords should be multiplied
     * @return Corresponding coordinates in voxels, in decimal numbers
     * @throws TransformException if unable to perform transformation.
     */
    public WorldCoords2d convert(Coords2d coords, double factor) throws TransformException {
        Coordinate coordinate = new Coordinate();
        try {
            preTransform.transform(coords.toCoordinate(), coordinate);
            Position position = crsTransform.transform(toPosition(coordinate), null);
            postTransform.transform(toCoordinate(position), coordinate);
            return new WorldCoords2d(coordinate.x * factor, coordinate.y * factor);
        } catch (MismatchedDimensionException e) {
            // This should never occur as we check dimensions in constructor
            return null;
        }
    }

    /**
     * Converts map coordinates into world coordinates.
     *
     * @param coords Coordinates in map
     * @return Corresponding coordinates in voxels
     * @throws TransformException if unable to perform transformation.
     */
    public WorldCoords2d convert(Coords2d coords) throws TransformException {
        return convert(coords, 1);
    }

    /**
     * Converts a geometry into world coordinates.
     *
     * @param geom Geometry to transform.
     * @return Transformed geometry (into world coordinates).
     * @throws TransformException if unable to perform transformation.
     */
    public Geometry convert(Geometry geom) throws TransformException {
        return postTransform.transform(JTS.transform(preTransform.transform(geom), crsTransform));
    }

    private Coordinate toCoordinate(WorldCoords2d coords, double factor) {
        return new Coordinate(coords.x() * factor, coords.y() * factor);
    }

    private Coordinate toCoordinate(Position position) {
        return new Coordinate(position.getOrdinate(0), position.getOrdinate(1));
    }

    private Position toPosition(Coordinate coordinate) {
        return new Position2D(coordinate.x, coordinate.y);
    }

    /**
     * Converts world coordinates back into map coordinates applying a factor.
     * This is usefull to handle millivoxels for example (with factor .001).
     * @param coords Coordinates in voxels
     * @param factor Factor by which coords should be multiplied
     * @return Corresponding coordinates in map
     * @throws TransformException if unable to perform transformation.
     */
    public Coords2d reverse(WorldCoords2d coords, double factor) throws TransformException {
        if (!invertible) return null; // TODO: Exeption?

        Coordinate coordinate = new Coordinate();
        try {
            postTransformInverse.transform(toCoordinate(coords, factor), coordinate);
            Position position = crsTransformInverse.transform(toPosition(coordinate), null);
            preTransformInverse.transform(toCoordinate(position), coordinate);
            return new Coords2d(coordinate);
        } catch (MismatchedDimensionException e) {
            // This should never occur as we check dimensions in constructor
            return null;
        }
    }

    /**
     * Converts world coordinates back into map coordinates.
     *
     * @param coords Coordinates in voxels
     * @return Corresponding coordinates in map
     * @throws TransformException if unable to perform transformation.
     */
    public Coords2d reverse(WorldCoords2d coords) throws TransformException {
        return reverse(coords, 1);
    }
}
