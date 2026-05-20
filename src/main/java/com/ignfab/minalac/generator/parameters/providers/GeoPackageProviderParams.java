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
import com.ignfab.minalac.generator.inputs.GeoPackageDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.utils.FileHelpers;

/**
 * Parameters for GeoPackage providers.
 */
public class GeoPackageProviderParams extends ProviderParams {
    /**
     * Path to GPKG file (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String filePath;

    /**
     * Type of features to read (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String typeName;

    /**
     * Coordinate reference system to use when reading data (optional, default: none).
     * By default, the CRS is read from the GeoPackage itself. You should only use this
     * parameter if the CRS is invalid or missing from the file.
     * This DOES NOT reproject data!
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String crsOverride;

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

        return new GeoPackageDataProvider(file, typeName, crsOverride, generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new GeoToolsVectorProcessorParams();
    }
}
