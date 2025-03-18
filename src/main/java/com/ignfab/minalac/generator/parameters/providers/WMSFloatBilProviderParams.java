package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.WMSFloatBilDataProvider;

/**
 * Parameters for WMS float providers.
 */
public class WMSFloatBilProviderParams extends ProviderParams {
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
     * Creates a new WFSProviderParams with mandatory fields.
     *
     * @param url Base URL for WFS queries (including protocol, port, domain name and directories but not query arguments)
     * @param layer Type of features to ask for
     */
    @ConstructorProperties({"url", "layer"})
    public WMSFloatBilProviderParams(String url, String layer) {
        this.url = url;
        this.layer = layer;
    }

    @Override
    public Provider<FloatGeographicDataMatrix2d> create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        if (crs != null)
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        else
            layerCrs = generation.crs();

        return new WMSFloatBilDataProvider(url, layer, layerCrs, generation::getEnvelopeForCRS);
    }
}
