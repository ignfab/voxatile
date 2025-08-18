package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.ShapefileDataProvider;
import com.ignfab.minalac.generator.utils.FileHelpers;

/**
 * Parameters for Shapefile providers.
 */
public class ShapefileProviderParams extends ProviderParams {
    /**
     * Path to Shapefile (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String filePath;

    /**
     * Coordinate reference system to use when reading data (optional, default: none).
     * By default, the CRS is read from the Shapefile itself. You should only use this
     * parameter if the CRS is invalid or missing from the file.
     * This DOES NOT reproject data!
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String crsOverride;

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
        CoordinateReferenceSystem crsOverride;
        if (this.crsOverride != null)
            try {
                crsOverride = CRS.decode(this.crsOverride);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(this.crsOverride), e);
            }
        else
            crsOverride = null;

        File file = new File(filePath);
        if (!FileHelpers.isReadableRegularFile(file))
            throw new IllegalArgumentException("File \"%s\" does not exist".formatted(file.getAbsolutePath()));

        return new ShapefileDataProvider(file, crsOverride, generation::getEnvelopeForCRS);
    }
}
