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
import com.ignfab.minalac.generator.inputs.WMSFeatureInfoDataProvider;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;

/**
 * Parameters for WMS GetFeatureInfo providers.
 */
public class WMSFeatureInfoProviderParams extends ProviderParams {
    /**
     * Base URL for WMS queries (required).
     */
    public String url;

    /**
     * Layer to query (required).
     */
    public String layer;

    /**
     * Coordinate reference system (optional, default: target CRS).
     * <p>
     * Must be advertised by the service for this layer. ASIG supports
     * {@code EPSG:6870}, {@code EPSG:4326}, {@code CRS:84} and {@code EPSG:900913}.
     */
    public String crs;

    /**
     * Pixel size of a query, in CRS units (optional, default: 1).
     * <p>
     * Must place {@code resolution / 0.00028} inside the scale window declared by the
     * layer, otherwise the server answers an empty collection without any error. On
     * ASIG: 0.14 to 1.40 for {@code adresar:adr_ndertese}, 0.06 to 0.70 for
     * {@code zrpp:ndertesa_qkd_042025} and the INSTAT census layers, 0.06 to 14 for
     * the ASHK cadastre.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double resolution = 1;

    /**
     * Half-width of a single query bounding box, in CRS units (optional, default: 70).
     * <p>
     * Together with {@code resolution} it sets the size of the query image, so keep
     * their ratio reasonable: 70 at a resolution of 1 gives a 141 px query.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double queryRadius = 70;

    /**
     * Distance between two query points, in CRS units (optional, default: 50).
     * <p>
     * Must not exceed the radius the server actually harvests, which scales with
     * {@code resolution} since the buffer cap is expressed in pixels: measured around
     * 76 m at a resolution of 1.33 on ASIG, around 30 m at 0.5.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double spacing = 50;

    /**
     * Value of the WMS {@code BUFFER} parameter, in pixels (optional, default: 100).
     * <p>
     * Servers cap this value, so raising it past the cap has no effect.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int buffer = 100;

    /**
     * Maximum features fetched at once (optional, default: 1000).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int maxFeaturesPerQuery = 1000;

    /**
     * Creates a new WMSFeatureInfoProviderParams with mandatory fields.
     *
     * @param url Base URL for WMS queries (including protocol, port, domain name and path but not query arguments)
     * @param layer Name of the WMS layer to query
     */
    @ConstructorProperties({"url", "layer"})
    public WMSFeatureInfoProviderParams(String url, String layer) {
        this.url = url;
        this.layer = layer;
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

        if (resolution <= 0)
            throw new IllegalArgumentException("Resolution must be positive, got %s".formatted(resolution));
        if (queryRadius <= 0)
            throw new IllegalArgumentException("Query radius must be positive, got %s".formatted(queryRadius));
        if (spacing <= 0)
            throw new IllegalArgumentException("Spacing must be positive, got %s".formatted(spacing));

        return new WMSFeatureInfoDataProvider(
            url,
            layer,
            layerCrs,
            resolution,
            queryRadius,
            spacing,
            buffer,
            maxFeaturesPerQuery,
            generation::getEnvelopeForCRS
        );
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new GeoToolsVectorProcessorParams();
    }
}
