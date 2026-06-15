package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;

/**
 * Parameters for WFS providers.
 */
public class WFSProviderParams extends ProviderParams {
    /**
     * Base URL for WFS queries (required).
     */
    public String url;

    /**
     * Type of features to fetch (required).
     */
    public String features;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

    /**
     * Token to use for authentication (optional, default: none).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String token;

    /**
     * Maximum features fetched at once (optional, default: 1000).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int maxFeaturesPerQuery = 1000;

    /**
     * Creates a new WFSProviderParams with mandatory fields.
     *
     * @param url Base URL for WFS queries (including protocol, port, domain name and path but not query arguments)
     * @param features Type of features to ask for
     */
    @ConstructorProperties({"url", "features"})
    public WFSProviderParams(String url, String features) {
        this.url = url;
        this.features = features;
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

        return new WFS1_1_GML3_1_DataProvider(url, features, layerCrs, generation::getEnvelopeForCRS, maxFeaturesPerQuery, token);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new GeoToolsVectorProcessorParams();
    }

}
