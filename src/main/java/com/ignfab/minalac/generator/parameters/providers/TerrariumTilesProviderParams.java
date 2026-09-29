package com.ignfab.minalac.generator.parameters.providers;

import java.awt.image.BufferedImage;
import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.XYZTilesProvider;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.TerrariumImageProcessorParams;

/**
 * Parameters for generic XYZ tiles providers.
 */
public class TerrariumTilesProviderParams extends ProviderParams {
    /**
     * Tile URL carrying the {@code {z}}, {@code {x}} and {@code {y}} placeholders
     * (required).
     */
    public String url;

    /**
     * Zoom level to read (optional, default: 12).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int zoom = 12;

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
    public Provider<BufferedImage> create(Generation generation) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("An XYZ tiles provider needs a url");
        if (!url.contains("{z}") || !url.contains("{x}") || !url.contains("{y}"))
            throw new IllegalArgumentException("The url must carry the {z}, {x} and {y} placeholders, got \"%s\"".formatted(url));

        return new XYZTilesProvider(url, zoom, generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new TerrariumImageProcessorParams();
    }
}
