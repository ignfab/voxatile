package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.StringReplacementFetcher;
import com.ignfab.minalac.generator.fetchers.WFS1_1_Fetcher;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.providers.GML3_1_Provider;
import com.ignfab.minalac.generator.providers.Provider;

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

        Fetcher fetcher = new WFS1_1_Fetcher(url, features, layerCrs, generation::getEnvelopeForCRS, maxFeaturesPerQuery);
        // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
        fetcher = new StringReplacementFetcher(fetcher, Map.of("http://BDTOPO_V3", "explicitly-invalid"));
        return new GML3_1_Provider(fetcher, layerCrs);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new GeoToolsVectorProcessorParams();
    }

}
