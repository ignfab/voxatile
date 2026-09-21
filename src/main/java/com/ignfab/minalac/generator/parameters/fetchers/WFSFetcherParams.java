package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.WFS1_1_Fetcher;
import com.ignfab.minalac.generator.generation.Generation;

/**
 * Parameters for {@link WFS1_1_Fetcher}.
 */
public class WFSFetcherParams extends HttpFetcherParams {
    /**
     * Base URL for WFS queries (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String url;

    /**
     * Type of features to fetch (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String features;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String crs;

    /**
     * Maximum features fetched at once (optional, default: 1000).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int maxFeaturesPerQuery = 1000;

    /**
     * Creates a new {@code WFSFetcherParams} with mandatory fields.
     *
     * @param url Base URL for WFS queries (including protocol, port, domain name and path but not query arguments)
     * @param features Type of features to ask for
     */
    @ConstructorProperties({ "url", "features" })
    public WFSFetcherParams(String url, String features) {
        this.url = url;
        this.features = features;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if (url.isBlank())
            throw new IllegalArgumentException("'url' must not be empty or blank");
        if (features.isBlank())
            throw new IllegalArgumentException("'features' must not be empty or blank");
        if (maxFeaturesPerQuery <= 0)
            throw new IllegalArgumentException("'maxFeaturesPerQuery' must be positive");
    }

    @Override
    public Fetcher create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        if (crs != null) {
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        } else
            layerCrs = generation.crs();

        return new WFS1_1_Fetcher(createHttpInit(), url, features, layerCrs, generation::getEnvelopeForCRS, maxFeaturesPerQuery);
    }
}
