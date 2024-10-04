package com.ignfab.minalac.generator.utils.coordinates;

import com.ignfab.minalac.generator.exceptions.TransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * An {@code EnvelopeProvider} computes envelope in a given CRS.
 * This is useful to get an envelope without knowing the CRS yet.
 * @see #computeForCRS(CoordinateReferenceSystem)
 */
@FunctionalInterface
public interface EnvelopeProvider {
    /**
     * Computes and returns the envelope in the given CRS.
     * @param crs the CRS to encode coordinates in
     * @return the resulting envelope
     * @see com.ignfab.minalac.generator.generation.Generation#getEnvelopeForCRS(CoordinateReferenceSystem)
     */
    ReferencedEnvelope computeForCRS(CoordinateReferenceSystem crs) throws FactoryException, TransformException;
}
