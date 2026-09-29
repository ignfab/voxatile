package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.WMSFetcher;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;

/**
 * Parameters for {@link WMSFetcher}.
 */
public class WMSFetcherParams extends HttpFetcherParams {
    /**
     * Base URL for WFS queries (required).
     */
    public String url;

    /**
     * Layer to fetch (required).
     */
    public String layer;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

    /**
     * Creates a new WMSFloatBilProviderParams with mandatory fields.
     *
     * @param url Base URL for WFS queries (including protocol, port, domain name and directories but not query arguments)
     * @param layer Type of features to ask for
     */
    @ConstructorProperties({ "url", "layer" })
    public WMSFetcherParams(String url, String layer) {
        this.url = url;
        this.layer = layer;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if (url.isBlank())
            throw new IllegalArgumentException("'url' must not be empty or blank");
        if (layer.isBlank())
            throw new IllegalArgumentException("'layer' must not be empty or blank");
    }

    @Override
    public Fetcher create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        if (crs == null)
            layerCrs = generation.crs();
        else {
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        }

        return new WMSFetcher(createHttpInit(), url, layer, layerCrs, generation::getEnvelopeForCRS);
    }
}
