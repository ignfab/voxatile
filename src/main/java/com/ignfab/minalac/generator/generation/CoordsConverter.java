package com.ignfab.minalac.generator.generation;

import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

/**
 * Converts coordinates from a CRS to world coordinates.
 */
public class CoordsConverter {
    private final MathTransform crsTransform;
    private final AffineTransformation postTransform;

    /**
     * Constructs a new {@code CoordsConverter}.
     * <p>
     * This is up to the caller to give proper transformations:
     * @param crsTransform Transformation from source CRS to world CRS.
     * @param postTransform Affine transformation applied for scale and rotation after CRS transformation.
     * @throws IllegalArgumentException if crsTransformation is not two dimensional.
     */

    public CoordsConverter(MathTransform crsTransform, AffineTransformation postTransform) {
        if (crsTransform.getSourceDimensions() != 2 || crsTransform.getTargetDimensions() != 2)
            throw new IllegalArgumentException("crsTransform must be from two dimensions to two dimensions");

        this.crsTransform = crsTransform;
        this.postTransform = postTransform;
    }

    /**
     * Converts a geometry into world coordinates.
     *
     * @param geom Geometry to transform.
     * @return Transformed geometry (into world coordinates).
     * @throws TransformException if unable to perform transformation.
     */
    public Geometry convert(Geometry geom) throws TransformException {
        return postTransform.transform(JTS.transform(geom, crsTransform));
    }
}
