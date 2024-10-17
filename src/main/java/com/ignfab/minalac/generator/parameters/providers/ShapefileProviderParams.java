package com.ignfab.minalac.generator.parameters.providers;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.ShapefileDataProvider;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import java.beans.ConstructorProperties;
import java.io.File;

/**
 * Parameters for Shapefile providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ShapefileProviderParams extends ProviderParams {
    /**
     * Path to Shapefile (required).
     */
    public String filePath;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

    /**
     * Creates a new ShapefileProviderParams with mandatory fields.
     *
     * @param filePath Path to Shapefile (absolute, or relative to current execution context)
     */
    @ConstructorProperties({"filePath"})
    public ShapefileProviderParams(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Provider<SimpleFeature> create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        if (crs != null)
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        else
            layerCrs = generation.crs();

        ReferencedEnvelope envelope;
        try {
            envelope = generation.getEnvelopeForCRS(layerCrs);
        } catch (FactoryException | TransformException e) {
            throw new IllegalArgumentException("Unable to compute envelope for given CRS", e);
        }

        File file = new File(filePath);
        if (!file.isFile())
            throw new IllegalArgumentException("File \"%s\" does not exist".formatted(file.getAbsolutePath()));

        return new ShapefileDataProvider(file, envelope);
    }
}
