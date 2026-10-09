package fr.ign.voxatile.core.parameters.providers;

import java.beans.ConstructorProperties;

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
     * URL carrying the {@code {z}}, {@code {x}} and {@code {y}} placeholders (required).
     *
     * <p>
     * Example: {@code https://example.com/{z}/{x}/{y}.png}
     */
    public String url;

    /**
     * Zoom level for the tiles (required).
     *
     * <p>
     * Defines the scale or detail level of the tiles.
     * Usually, 0 defines the whole world, and 18 or higher defines the street level.
     * However, this depends on the specific tile provider and the available zoom levels.
     *
     * <p>
     * Example: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Zoom_levels
     */
    public int zoom;

    // TODO: field for float decoder
    // public FloatDecoderParams format;

    /**
     * Constructs a new {@link XYZTilesProviderParams} with mandatory fields.
     *
     * @param url URL carrying the {@code {z}}, {@code {x}} and {@code {y}} placeholders
     * @param zoom zoom level for the tiles
     */
    @ConstructorProperties({"url", "zoom"})
    public XYZTilesProviderParams(String url, int zoom) {
        this.url = url;
        this.zoom = zoom;
    }

    @Override
    public Provider<FloatGeographicDataMatrix2d> create(Generation generation) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("An XYZ tiles provider needs a url");
        if (!url.contains("{x}") || !url.contains("{y}") || !url.contains("{z}"))
            throw new IllegalArgumentException("The url must carry the {x}, {y} and {z} placeholders, got \"%s\"".formatted(url));
        if (zoom < 0)
            throw new IllegalArgumentException("The zoom level must be non-negative, got \"%s\"".formatted(zoom));

        // TODO: Handle more float decoder
        return new XYZTilesProvider(url, zoom, new TerrariumFloatDecoder(), generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new FloatMatrixProcessorParams();
    }
}
