package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * An {@code EnvelopeProvider} computes envelope in a given CRS.
 * This is useful to get an envelope without knowing the CRS yet.
 * @see #computeForCRS(CoordinateReferenceSystem, WorldBBox3d)
 */
@FunctionalInterface
public interface EnvelopeProvider {
    /**
     * Computes and returns an envelope in the given CRS for a given bounding box.
     * @param crs the CRS to encode coordinates in
     * @param bbox bounding box (in world coordinates) to create envelope for
     * @return the resulting envelope
     * @see com.ignfab.minalac.generator.generation.Generation#getEnvelopeForCRS(CoordinateReferenceSystem, WorldBBox3d)
     */
    ReferencedEnvelope computeForCRS(CoordinateReferenceSystem crs, WorldBBox3d bbox) throws FactoryException, TransformException;
}
