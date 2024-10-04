package com.ignfab.minalac.generator.parameters.providers;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.GeoPackageDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import java.beans.ConstructorProperties;
import java.io.File;

/**
 * Parameters for GeoPackage providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class GeoPackageProviderParams extends ProviderParams {
    /**
     * Path to GPKG file (required).
     */
    public String filePath;

    /**
     * Type of features to read (required).
     */
    public String typeName;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

    /**
     * Creates a new GeoPackageProviderParams with mandatory fields.
     *
     * @param filePath Path to GPKG file (absolute, or relative to current execution context)
     * @param typeName Type of features to read inside the file
     */
    @ConstructorProperties({"filePath", "typeName"})
    public GeoPackageProviderParams(String filePath, String typeName) {
        this.filePath = filePath;
        this.typeName = typeName;
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

        return new GeoPackageDataProvider(file, typeName, envelope);
    }
}
