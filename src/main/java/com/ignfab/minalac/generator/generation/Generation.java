package com.ignfab.minalac.generator.generation;

import org.geotools.referencing.CRS;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.MathTransform;

import org.locationtech.jts.geom.util.AffineTransformation;

/**
 * Generation context class.
 *
 * Contains stuff about ongoing generation and its context.
 */
public class Generation {
    private CoordinateReferenceSystem crs;
    private AffineTransformation transformation;

    /**
     * Constructs a new generation context.
     *
     * @param crs Coordinate reference system used for generated world.
     * @param transformation Two dimensions affine transformation applied to world (usually rotation + scale).
     */
    public Generation(CoordinateReferenceSystem crs, AffineTransformation transformation) {
        this.crs = crs;
        this.transformation = transformation;
    }

    /**
     * Creates a converter for a given CRS.
     *
     * @param sourceCrs CRS from which convert map coordinates.
     * @return A converter to be used to convert any map coordinates into generated world coordinates.
     * @throws FactoryException If not suitable transformation found for conversion.
     */
    public CoordsConverter makeCoordsConverter(CoordinateReferenceSystem sourceCrs) throws FactoryException {
        MathTransform crsTransform = CRS.findMathTransform(sourceCrs, crs);
        return new CoordsConverter(crsTransform, transformation);
    }
}