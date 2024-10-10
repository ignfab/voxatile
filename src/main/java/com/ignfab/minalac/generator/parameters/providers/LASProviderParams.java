package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.io.File;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASDataProvider;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.inputs.Provider;

/**
 * Parameters for LAS providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class LASProviderParams extends ProviderParams {
    /**
     * Path to LAS/LAZ file (required).
     */
    public String filePath;

    /**
     * Coordinate reference system of points inside file (required).
     */
    public String crs;

    /**
     * Creates a new LASProviderParams with mandatory fields.
     *
     * @param filePath Path to LAS/LAZ file (absolute, or relative to current execution context)
     * @param crs Coordinate reference system of the points inside the file
     */
    @ConstructorProperties({"filePath", "crs"})
    public LASProviderParams(String filePath, String crs) {
        this.filePath = filePath;
        this.crs = crs;
    }

    @Override
    public Provider<LASPointAndHeader> create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        try {
            layerCrs = CRS.decode(crs);
        } catch (FactoryException e) {
            throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
        }

        ReferencedEnvelope envelope;
        try {
            envelope = generation.getEnvelopeForCRS(layerCrs);
        } catch (FactoryException | TransformException e) {
            throw new IllegalArgumentException("Unable to compute envelope for given CRS", e);
        }

        File file = new File(filePath);
        if (!file.isFile())
            throw new IllegalArgumentException("File \"%s\" does not exist".formatted(file.getAbsolutePath()));

        return new LASDataProvider(file, envelope);
    }
}
