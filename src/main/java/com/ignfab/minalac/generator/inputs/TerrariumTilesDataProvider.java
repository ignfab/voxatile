package com.ignfab.minalac.generator.inputs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider reading elevations from Terrarium-encoded raster tiles served over
 * a plain {@code {z}/{x}/{y}} endpoint.
 * <p>
 * Terrarium packs an elevation in the three channels of an ordinary image:
 * {@code height = red * 256 + green + blue / 256 - 32768}, in metres. Unlike a
 * shaded relief, which encodes the slope under a fixed light and cannot be
 * inverted, this is the elevation itself and decodes exactly.
 * <p>
 * <b>Known sources.</b>
 * <ul>
 *   <li>{@code https://s3.amazonaws.com/elevation-tiles-prod/terrarium/{z}/{x}/{y}.png}
 *       — AWS Terrain Tiles, 256 px PNG, no account, read by {@code ImageIO} out of
 *       the box.</li>
 *   <li>{@code https://tiles.mapterhorn.com/{z}/{x}/{y}.webp} — Mapterhorn, 512 px
 *       WebP, no account, twice the pixel density at the same zoom. {@code ImageIO}
 *       has no WebP reader in the standard library, so this source needs a plugin
 *       such as {@code com.twelvemonkeys.imageio:imageio-webp}. Attribution
 *       required.</li>
 * </ul>
 * Over Albania both stop at zoom 12: about 28 m per pixel on 256 px tiles, 14 m on
 * 512 px ones. Deeper zooms only cover countries that contributed a national LiDAR
 * survey, and answer 404 elsewhere.
 * <p>
 * <b>Sampling.</b> Tiles are in Web Mercator while the generator works in its own
 * CRS, so the grid is walked backwards: each output row is projected to longitude
 * and latitude in one call, then every cell reads the pixel below it. Tiles are
 * downloaded once and kept for the whole tile of the world being generated.
 * <p>
 * Transient failures are reported as {@link RetryableException} and left to the
 * retry policy of the task, rather than retried here.
 */
public class TerrariumTilesDataProvider implements Provider<FloatGeographicDataMatrix2d> {
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 60_000;

    /** Guards against an unbounded grid when a tile is large and the resolution fine. */
    private static final int MAX_GRID_SIZE = 4096;

    /** Web Mercator is undefined beyond these latitudes. */
    private static final double MAX_LATITUDE = 85.0511287798066;

    private final String urlTemplate;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final MathTransform toWGS84;
    private final double resolution;
    private final int zoom;

    /**
     * Creates a new {@code TerrariumTilesDataProvider}.
     *
     * @param urlTemplate tile URL carrying the {@code {z}}, {@code {x}} and
     *                    {@code {y}} placeholders
     * @param zoom zoom level to read; the deepest level published by the source gives
     *             the finest elevations, deeper ones answer 404
     * @param resolution size of an output cell, in CRS units
     * @param crs coordinate reference system of the produced matrix
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public TerrariumTilesDataProvider(
            String urlTemplate,
            int zoom,
            double resolution,
            CoordinateReferenceSystem crs,
            EnvelopeProvider envelopeProvider
    ) {
        if (zoom < 0)
            throw new IllegalArgumentException("Zoom must not be negative, got " + zoom);
        if (resolution <= 0)
            throw new IllegalArgumentException("Resolution must be positive, got " + resolution);

        this.urlTemplate = urlTemplate;
        this.zoom = zoom;
        this.resolution = resolution;
        this.crs = crs;
        this.envelopeProvider = envelopeProvider;

        try {
            // DefaultGeographicCRS.WGS84 is longitude first, so the output of the
            // transform needs no further axis juggling.
            this.toWGS84 = CRS.findMathTransform(crs, DefaultGeographicCRS.WGS84, true);
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Cannot project from " + crs.getName() + " to WGS84", e);
        }
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Result<FloatGeographicDataMatrix2d> provide(WorldBBox3d bbox)
            throws GenerationFailedException, RetryableException {

        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        int width = cellCount(envelope.getWidth());
        int height = cellCount(envelope.getHeight());
        double stepX = envelope.getWidth() / width;
        double stepY = envelope.getHeight() / height;

        FloatArrayGeographicDataMatrix2d result = new FloatArrayGeographicDataMatrix2d(
            width, height, envelope.getMinX(), envelope.getMinY(), stepX, stepY);

        Map<Long, BufferedImage> tiles = new HashMap<>();
        float[] data = result.data();
        double[] row = new double[2 * width];

        for (int j = 0; j < height; j++) {
            double y = envelope.getMinY() + (j + 0.5) * stepY;
            for (int i = 0; i < width; i++) {
                row[2 * i] = envelope.getMinX() + (i + 0.5) * stepX;
                row[2 * i + 1] = y;
            }

            try {
                toWGS84.transform(row, 0, row, 0, width);
            } catch (org.geotools.api.referencing.operation.TransformException e) {
                throw new GenerationFailedException("Cannot project the envelope to WGS84", e);
            }

            for (int i = 0; i < width; i++)
                data[j * width + i] = elevation(tiles, row[2 * i], row[2 * i + 1]);
        }

        return new SimpleResult<>(crs, Iterators.iterator(result));
    }

    private int cellCount(double span) {
        long cells = Math.round(span / resolution);
        return (int) Math.max(1, Math.min(MAX_GRID_SIZE, cells));
    }

    /**
     * Reads the elevation under a position, downloading the tile that carries it the
     * first time it is needed.
     *
     * @param tiles tiles already downloaded, used and filled in
     * @param longitude the longitude to sample
     * @param latitude the latitude to sample
     * @return the elevation in metres, or zero where the source publishes no tile
     */
    private float elevation(Map<Long, BufferedImage> tiles, double longitude, double latitude)
            throws GenerationFailedException, RetryableException {

        double fx = fractionalX(longitude);
        double fy = fractionalY(latitude);
        int x = clamp((int) Math.floor(fx));
        int y = clamp((int) Math.floor(fy));
        long key = ((long) x << 32) | (y & 0xFFFFFFFFL);

        BufferedImage tile = tiles.get(key);
        if (tile == null) {
            if (tiles.containsKey(key))
                return 0f;                       // known gap in the coverage
            tile = download(x, y);
            tiles.put(key, tile);
            if (tile == null)
                return 0f;
        }

        int size = tile.getWidth();
        int i = Math.min(size - 1, Math.max(0, (int) ((fx - x) * size)));
        int j = Math.min(size - 1, Math.max(0, (int) ((fy - y) * size)));

        int rgb = tile.getRGB(i, j);
        return ((rgb >> 16) & 0xFF) * 256f + ((rgb >> 8) & 0xFF) + (rgb & 0xFF) / 256f - 32768f;
    }

    /**
     * Downloads and decodes one tile.
     *
     * @param x tile column
     * @param y tile row
     * @return the tile, or {@code null} when the source publishes none there
     */
    private BufferedImage download(int x, int y)
            throws GenerationFailedException, RetryableException {

        String url = urlTemplate
            .replace("{z}", Integer.toString(zoom))
            .replace("{x}", Integer.toString(x))
            .replace("{y}", Integer.toString(y));

        try {
            URLConnection connection = URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);

            if (connection instanceof HttpURLConnection http) {
                int status = http.getResponseCode();
                // Outside the published coverage the source answers 404. That is an
                // answer, not a failure: those cells are simply left at zero.
                if (status == HttpURLConnection.HTTP_NOT_FOUND)
                    return null;
                if (status != HttpURLConnection.HTTP_OK)
                    throw new RetryableException("Tile " + url + " returned HTTP " + status);
            }

            try (InputStream stream = connection.getInputStream()) {
                BufferedImage image = ImageIO.read(stream);
                if (image == null)
                    throw new GenerationFailedException("No image reader for " + url
                        + "; a WebP source needs an ImageIO plugin on the classpath");
                return image;
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new GenerationFailedException("Invalid tile URL " + url, e);
        } catch (IOException e) {
            throw new RetryableException("Error reading tile " + url, e);
        }
    }

    // --- Web Mercator tiling ---------------------------------------------

    private double fractionalX(double longitude) {
        return (longitude + 180.0) / 360.0 * (1 << zoom);
    }

    private double fractionalY(double latitude) {
        double radians = Math.toRadians(Math.max(-MAX_LATITUDE, Math.min(MAX_LATITUDE, latitude)));
        return (1.0 - Math.log(Math.tan(radians) + 1.0 / Math.cos(radians)) / Math.PI)
            / 2.0 * (1 << zoom);
    }

    private int clamp(int index) {
        return Math.min((1 << zoom) - 1, Math.max(0, index));
    }
}
