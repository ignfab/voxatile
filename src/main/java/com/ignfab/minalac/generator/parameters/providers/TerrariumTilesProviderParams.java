package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.TerrariumTilesDataProvider;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;

/**
 * Parameters for Terrarium tiles providers.
 */
public class TerrariumTilesProviderParams extends ProviderParams {
    /**
     * Tile URL carrying the {@code {z}}, {@code {x}} and {@code {y}} placeholders
     * (required).
     */
    public String url;

    /**
     * Zoom level to read (optional, default: 12).
     * <p>
     * The deepest level actually published gives the finest elevations. Asking for
     * more answers 404 and leaves the area empty, which is why this is not raised by
     * default: over Albania, 12 is the maximum for both known sources.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int zoom = 12;

    /**
     * Size of an output cell, in CRS units (optional, default: 30).
     * <p>
     * Matching the native resolution of the tiles avoids both interpolating and
     * throwing data away: roughly 28 m on 256 px tiles at zoom 12, 14 m on 512 px
     * ones.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double resolution = 30;

    /**
     * Coordinate reference system of the produced matrix (optional, default: target
     * CRS).
     */
    public String crs;

    /**
     * Creates a new {@code TerrariumTilesProviderParams} with mandatory fields.
     *
     * @param url Tile URL with the {@code {z}}, {@code {x}} and {@code {y}} placeholders
     */
    @ConstructorProperties({"url"})
    public TerrariumTilesProviderParams(String url) {
        this.url = url;
    }

    @Override
    public Provider<FloatGeographicDataMatrix2d> create(Generation generation) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("A terrarium provider needs a url");
        if (!url.contains("{z}") || !url.contains("{x}") || !url.contains("{y}"))
            throw new IllegalArgumentException(
                "The url must carry the {z}, {x} and {y} placeholders, got \"%s\"".formatted(url));

        CoordinateReferenceSystem layerCrs;
        if (crs != null)
            try {
                layerCrs = CRS.decode(crs);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
            }
        else
            layerCrs = generation.crs();

        return new TerrariumTilesDataProvider(
            url, zoom, resolution, layerCrs, generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new FloatMatrixProcessorParams();
    }
}
