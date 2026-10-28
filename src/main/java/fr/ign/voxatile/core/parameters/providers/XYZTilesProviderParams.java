package fr.ign.voxatile.core.parameters.providers;

import java.beans.ConstructorProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.FloatGeographicDataMatrix2d;
import fr.ign.voxatile.core.inputs.Provider;
import fr.ign.voxatile.core.inputs.XYZTilesProvider;
import fr.ign.voxatile.core.inputs.decoders.TerrariumFloatDecoder;
import fr.ign.voxatile.core.parameters.processors.FloatMatrixProcessorParams;
import fr.ign.voxatile.core.parameters.processors.ProcessorParams;

/**
 * Parameters for {@link XYZTilesProvider}.
 */
public class XYZTilesProviderParams extends ProviderParams {
    /**
     * URL carrying the {@code {x}}, {@code {y}} and {@code {z}} placeholders (required).
     *
     * <p>
     * Example: {@code https://example.com/{z}/{x}/{y}.png}
     */
    public String url;

    /**
     * Minimum zoom level handle by the service used (required).
     *
     * <p>
     * Defines the minimum scale or detail level of the tiles.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int zoomMin = 0;

    /**
     * Maximum zoom level handle by the service used (required).
     *
     * <p>
     * Defines the maximum scale or detail level of the tiles.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int zoomMax = 12;

    // TODO: field for float decoder
    // public FloatDecoderParams format;

    /**
     * Constructs a new {@link XYZTilesProviderParams} with mandatory fields.
     *
     * @param url URL carrying the {@code {x}}, {@code {y}} and {@code {z}} placeholders
     * @param zoomMin minimum zoom level for the tiles
     * @param zoomMax maximum zoom level for the tiles
     */
    @ConstructorProperties({"url"})
    public XYZTilesProviderParams(String url) {
        this.url = url;
    }

    @Override
    public Provider<FloatGeographicDataMatrix2d> create(Generation generation) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("An XYZ tiles provider needs a url");
        if (!url.contains("{x}") || !url.contains("{y}") || !url.contains("{z}"))
            throw new IllegalArgumentException("The url must carry the {x}, {y} and {z} placeholders, got \"%s\"".formatted(url));
        if (zoomMin < 0)
            throw new IllegalArgumentException("The minimum zoom level must be non-negative, got \"%s\"".formatted(zoomMin));
        if (zoomMax < 0)
            throw new IllegalArgumentException("The maximum zoom level must be non-negative, got \"%s\"".formatted(zoomMax));

        // TODO: Handle more float decoder
        return new XYZTilesProvider(url, zoomMin, zoomMax, generation.getHorizontalScale(), new TerrariumFloatDecoder(), generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new FloatMatrixProcessorParams();
    }
}
