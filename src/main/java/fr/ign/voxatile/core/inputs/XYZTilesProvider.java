package fr.ign.voxatile.core.inputs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.datum.DefaultEllipsoid;

import fr.ign.voxatile.core.exceptions.GenerationFailedException;
import fr.ign.voxatile.core.exceptions.RetryableException;
import fr.ign.voxatile.core.exceptions.TransformException;
import fr.ign.voxatile.core.utils.coordinates.EnvelopeProvider;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

/**
 * Data provider reading images from generic XYZ URL endpoints.
 */
public class XYZTilesProvider implements Provider<IntegerGeographicDataMatrix2d> {

    /**
     * Coordinate reference system for the provider (EPSG:4326).
     */
    private static final CoordinateReferenceSystem CRS = DefaultGeographicCRS.WGS84;

    private static final double EARTH_CIRCUMFERENCE_METERS = 2. * Math.PI * DefaultEllipsoid.WGS84.getSemiMajorAxis();
    private static final double MAX_MERCATOR_LATITUDE = Math.toDegrees(Math.atan(Math.sinh(Math.PI)));

    private final String url;
    private final int zoomMin;
    private final int zoomMax;
    private final double horizontalScale;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code XYZTilesProvider}.
     *
     * @param url URL with {@code {x}}, {@code {y}}, and {@code {z}} placeholders
     * @param zoomMin minimum zoom level supported by the tile server
     * @param zoomMax maximum zoom level supported by the tile server
     * @param horizontalScale the horizontal scale for the tiles
     * @param envelopeProvider the provider used to compute the envelope for the given CRS and bounding box
     */
    public XYZTilesProvider(
            String url,
            int zoomMin,
            int zoomMax,
            double horizontalScale,
            EnvelopeProvider envelopeProvider
        ) {
        this.url = url;
        this.zoomMin = zoomMin;
        this.zoomMax = zoomMax;
        this.horizontalScale = horizontalScale;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<IntegerGeographicDataMatrix2d> providedType() {
        return IntegerGeographicDataMatrix2d.class;
    }

    @Override
    public Result<IntegerGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(CRS, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        int tileSize;
        try (InputStream stream = URI.create(url.replace("{z}", String.valueOf(zoomMin))
                                                .replace("{x}", "0")
                                                .replace("{y}", "0")).toURL().openStream()) {
            tileSize = ImageIO.read(stream).getWidth();
        } catch (Exception e) {
            throw new GenerationFailedException(e);
        }

        // Mercator distorts distances away from the equator. Use the average latitude
        // of the bounding box to apply the cos(latitude) scale factor.
        double centerLat = (envelope.getMinY() + envelope.getMaxY()) / 2.0;
        double latRad = Math.toRadians(Math.max(-MAX_MERCATOR_LATITUDE, Math.min(MAX_MERCATOR_LATITUDE, centerLat)));
        // Ground resolution at Zoom 0 at Equator = Earth Circumference / tile size (m/px)
        double initialResolution = EARTH_CIRCUMFERENCE_METERS / tileSize;

        // Inverted Web Mercator formula
        double exactZoom = Math.log((initialResolution * Math.cos(latRad)) / horizontalScale) / Math.log(2.0);
        int targetZoom = (int) Math.max(zoomMin, Math.min((int) Math.ceil(exactZoom), zoomMax));

        int numTiles = 1 << targetZoom;

        int minX = longitudeToX(envelope.getMinX(), numTiles);
        int minY = latitudeToY(envelope.getMaxY(), numTiles);
        int maxX = longitudeToX(envelope.getMaxX(), numTiles);
        int maxY = latitudeToY(envelope.getMinY(), numTiles);

        /*
         * Handle International Date Line crossing.
         *   Example: minX = 15 (far right) and maxX = 0 (far left).
         * A normal loop (from 15 to 0) would instantly stop, so we calculate the total number of tiles instead.
         */
        int countX = (maxX >= minX) ? (maxX - minX + 1) : (numTiles - minX + maxX + 1);

        List<IntegerGeographicDataMatrix2d> tiles = new ArrayList<>();
        for (int y = minY; y <= maxY; y++) {
            for (int stepX = 0; stepX < countX; stepX++) {

                /*
                 * Prevent 404 Errors when falling off the right edge of the world.
                 *   Example: Tile 15 + 1 step = 16. The modulo (16 % 16) wraps it seamlessly back to tile 0.
                 */
                int x = (minX + stepX) % numTiles;

                BufferedImage image;
                try (InputStream stream = URI.create(url.replace("{x}", String.valueOf(x))
                                                        .replace("{y}", String.valueOf(y))
                                                        .replace("{z}", String.valueOf(targetZoom))).toURL().openStream()) {
                    image = ImageIO.read(stream);
                } catch (IOException e) {
                    throw new GenerationFailedException(e);
                }

                if (image != null) {
                    double tileMinX = xToLongitude(x, numTiles);
                    double tileMaxY = yToLatitude(y, numTiles);

                    double cellSizeX = (xToLongitude(x + 1, numTiles) - tileMinX) / (tileSize - 1);
                    double cellSizeY = (tileMaxY - yToLatitude(y + 1, numTiles)) / (tileSize - 1);

                    double offsetX = tileMinX + (cellSizeX / 2.0);
                    double offsetY = tileMaxY - (cellSizeY / 2.0);

                    tiles.add(new IntegerImageGeographicDataMatrix2d(image, tileSize, tileSize, offsetX, offsetY, cellSizeX, cellSizeY));
                }
            }
        }

        return new SimpleResult<>(CRS, tiles.iterator());
    }

    // -------------------------------------------------------------------------
    // Slippy Map Coordinate Conversions
    // Based on: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    // -------------------------------------------------------------------------

    private static double xToLongitude(int x, int numTiles) {
        return x * 360.0 / numTiles - 180.0;
    }

    private static int longitudeToX(double lon, int numTiles) {
        return Math.max(0, Math.min((int) ((lon + 180.0) / 360.0 * numTiles), numTiles - 1));
    }

    private static double yToLatitude(int y, int numTiles) {
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2.0 * y / numTiles))));
    }

    private static int latitudeToY(double lat, int numTiles) {
        /*
         * The Web Mercator projection cannot display the North/South poles.
         * It mathematically breaks beyond ~85.0511 degrees latitude (Logarithm of zero/infinity).
         * So we clamp the latitude to this exact maximum limit.
         */
        double clampedLat = Math.max(-MAX_MERCATOR_LATITUDE, Math.min(MAX_MERCATOR_LATITUDE, lat));

        double latToRad = Math.toRadians(clampedLat);
        int y = (int) ((1 - Math.log(Math.tan(latToRad) + 1 / Math.cos(latToRad)) / Math.PI) / 2 * numTiles);

        return Math.max(0, Math.min(y, numTiles - 1));
    }
}
