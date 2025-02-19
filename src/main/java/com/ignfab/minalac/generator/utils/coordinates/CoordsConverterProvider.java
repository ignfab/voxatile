package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * A {@code CoordsConverterProvider} computes converter from a given CRS.
 * This is useful to get a coords converter without knowing the CRS yet.
 * @see #computeForCRS(CoordinateReferenceSystem)
 */
@FunctionalInterface
public interface CoordsConverterProvider {
    /**
     * Computes and returns the coords converter from the given CRS.
     * @param fromCrs the source CRS of the conversion
     * @return the resulting converter
     * @see com.ignfab.minalac.generator.generation.Generation#makeCoordsConverter(CoordinateReferenceSystem)
     */
    MapToWorldConverter computeForCRS(CoordinateReferenceSystem fromCrs) throws FactoryException;
}
