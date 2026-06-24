package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.io.File;

import org.citygml4j.core.model.core.AbstractCityObject;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.CityJSONDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.processors.CityJSONBuildingProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.utils.FileHelpers;

/**
 * Parameters for CityJSON providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CityJSONProviderParams extends ProviderParams {
    /**
     * Path to CityJSON file (required).
     */
    public String filePath;

    /**
     * Overriding coordinate reference system (optional, default: none).
     */
    public String overrideCrs;

    /**
     * Creates a new CityJSONProviderParams with mandatory fields.
     *
     * @param filePath Path to CityJSON file (absolute, or relative to current execution context)
     */
    @ConstructorProperties({"filePath"})
    public CityJSONProviderParams(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Provider<AbstractCityObject> create(Generation generation) {
        CoordinateReferenceSystem crs = null;
        if (overrideCrs != null)
            try {
                crs = CRS.decode(overrideCrs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }

        File file = new File(filePath);
        if (!FileHelpers.isReadableRegularFile(file))
            throw new IllegalArgumentException("File \"%s\" does not exist or is not readable".formatted(file.getAbsolutePath()));

        return new CityJSONDataProvider(file, crs, generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new CityJSONBuildingProcessorParams();
    }
}
