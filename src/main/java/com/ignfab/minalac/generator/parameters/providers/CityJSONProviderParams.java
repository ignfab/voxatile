package com.ignfab.minalac.generator.parameters.providers;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.CityJSONDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import java.beans.ConstructorProperties;
import java.io.File;

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
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

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

        return new CityJSONDataProvider(file, envelope);
    }
}
