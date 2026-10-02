package com.ignfab.minalac.generator.inputs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.coordinates.WebMercatorUtils;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider reading images from generic XYZ tile endpoints.
 */
public class XYZTilesProvider implements Provider<BufferedImage> {
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 60_000;

    private final String urlTemplate;
    private final EnvelopeProvider envelopeProvider;
    private final int zoom;

    /**
     * Creates a new {@code XYZTilesProvider} instance.
     *
     * @param urlTemplate the URL template for the XYZ tiles, with placeholders {z}, {x}, and {y}
     * @param zoom the zoom level for the tiles
     * @param envelopeProvider the provider used to compute the envelope for the given CRS and bounding box
     */
    public XYZTilesProvider(
            String urlTemplate,
            int zoom,
            EnvelopeProvider envelopeProvider
        ) {
        this.urlTemplate = urlTemplate;
        this.zoom = zoom;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<BufferedImage> providedType() {
        return BufferedImage.class;
    }

    @Override
    public Result<BufferedImage> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        ReferencedEnvelope wgs84Envelope;
        try {
            wgs84Envelope = envelopeProvider.computeForCRS(DefaultGeographicCRS.WGS84, bbox);
        } catch (Exception e) {
            throw new GenerationFailedException("Cannot compute WGS84 envelope", e);
        }

        int minTileX = WebMercatorUtils.clamp((int) Math.floor(WebMercatorUtils.fractionalX(wgs84Envelope.getMinX(), zoom)), zoom);
        int maxTileX = WebMercatorUtils.clamp((int) Math.floor(WebMercatorUtils.fractionalX(wgs84Envelope.getMaxX(), zoom)), zoom);
        int minTileY = WebMercatorUtils.clamp((int) Math.floor(WebMercatorUtils.fractionalY(wgs84Envelope.getMaxY(), zoom)), zoom); 
        int maxTileY = WebMercatorUtils.clamp((int) Math.floor(WebMercatorUtils.fractionalY(wgs84Envelope.getMinY(), zoom)), zoom);

        List<BufferedImage> tiles = new ArrayList<>();
        for (int x = minTileX; x <= maxTileX; x++)
            for (int y = minTileY; y <= maxTileY; y++) {
                BufferedImage image = download(x, y);
                if (image != null) tiles.add(image);
            }

        return new SimpleResult<>(DefaultGeographicCRS.WGS84, tiles.iterator());
    }

    // Downloads a single tile image for the given x and y coordinates at the configured zoom level.
    private BufferedImage download(int x, int y) throws GenerationFailedException, RetryableException {
        String url = urlTemplate.replace("{z}", String.valueOf(zoom))
                                .replace("{x}", String.valueOf(x))
                                .replace("{y}", String.valueOf(y));

        try {
            URLConnection connection = URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);

            if (connection instanceof HttpURLConnection http) {
                int status = http.getResponseCode();
                if (status == HttpURLConnection.HTTP_NOT_FOUND) return null;
                if (status != HttpURLConnection.HTTP_OK)
                    throw new RetryableException("Tile " + url + " returned HTTP " + status);
            }

            try (InputStream stream = connection.getInputStream()) {
                BufferedImage image = ImageIO.read(stream);
                if (image == null)
                    throw new GenerationFailedException("No image reader for " + url);
                return image;
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new RetryableException("Error or invalid URL reading tile " + url, e);
        }
    }
}
