package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.operation.MathTransform;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A map (real) to world (game) coordinates converter.
 *
 * This wraps a {@link Converter} and provide method with adapted types.
 */
public class MapToWorldConverter {
    private final Converter converter;

    /**
     * Creates a new {@code MapToWorldConverter} out of a {@code CoordinatesConverter}.
     *
     * @param converter base converter to use
     */
    protected MapToWorldConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * Creates a new {@code MapToWorldConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usually for scale and rotation).
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    public MapToWorldConverter(AffineTransformation preTransform, MathTransform crsTransform, AffineTransformation postTransform) {
        this(new Converter(preTransform, crsTransform, postTransform));
    }

    /**
     * Creates a new {@code MapToWorldConverter}.
     *
     * This is up to the caller to give proper transformations:
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied after CRS transformation (usualy for scale and rotation).
     * @throws IllegalArgumentException if crsTransformation is not two dimensional or any transformation is null.
     */
    public MapToWorldConverter(MathTransform crsTransform, AffineTransformation postTransform) {
        this(new AffineTransformation(), crsTransform, postTransform);
    }

    /**
     * Creates a new {@code MapToWorldConverter} from another, adding a preTransformation.
     *
     * This is up to the caller to give proper transformations:
     * @param preTransform Affine transformation applied before CRS transformation (usually for translation).
     * @param converter Converter to start from
     */
    public MapToWorldConverter(AffineTransformation preTransform, MapToWorldConverter converter) {
        this(new Converter(preTransform, converter.converter));
    }

    /**
     * Creates a new {@code WorldToMapCoordsConverter} performing inverse transformation.
     *
     * @return inverse coverter
     * @throws TransformException if coverter cannot be inverted
     */
    public WorldToMapConverter inverse() throws TransformException {
        return new WorldToMapConverter(converter.inverse());
    }

    /**
     * Converts map (real) coordinates into world (game) coordinates.
     *
     * @param coords Coordinates in map
     * @return Corresponding coordinates in voxels, in decimal numbers
     * @throws TransformException if unable to perform transformation.
     */
    public WorldCoords2d convert(MapCoordinates coords) throws TransformException {
        MapCoordinates converted = converter.convert(coords);
        return WorldCoords2d.floor(converted.x(), converted.y());
    }

    /**
     * Converts a JTS geometry.
     *
     * @param geom Geometry to transform.
     * @return Transformed geometry.
     * @throws TransformException if unable to perform transformation.
     */
    public Geometry convert(Geometry geom) throws TransformException {
        return converter.convert(geom);
    }

    public MapCoordinates convertRaw(MapCoordinates coords) throws TransformException {
        return converter.convert(coords);
    }
}
