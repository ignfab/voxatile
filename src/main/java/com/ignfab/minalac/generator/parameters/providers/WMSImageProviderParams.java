package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.IntegerGeographicDataMatrix2d;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.WMSImageProvider;

/**
 * Parameters for WMS image providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class WMSImageProviderParams extends ProviderParams {
    /**
     * Base URL for WMS queries (required).
     */
    public String url;

    /**
     * Layer to fetch (required).
     */
    public String layer;

    /**
     * Image format to ask for (required).
     */
    public String format;

    /**
     * Coordinate reference system (optional, default: target CRS).
     */
    public String crs;

    /**
     * Creates a new WFSImageProviderParams with mandatory fields.
     *
     * @param url Base URL for WMS queries
     * @param layer Layer to fetch
     */
    @ConstructorProperties({"url", "layer", "format"})
    public WMSImageProviderParams(String url, String layer) {
        this.url = url;
        this.layer = layer;
    }

    @Override
    public Provider<IntegerGeographicDataMatrix2d> create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        if (crs != null)
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        else
            layerCrs = generation.crs();

        return new WMSImageProvider(url, layer, format, layerCrs, generation::getEnvelopeForCRS);
    }
}
