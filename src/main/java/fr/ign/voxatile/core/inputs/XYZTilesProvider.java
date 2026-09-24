package fr.ign.voxatile.core.inputs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import javax.imageio.ImageIO;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import fr.ign.voxatile.core.exceptions.GenerationFailedException;
import fr.ign.voxatile.core.exceptions.RetryableException;
import fr.ign.voxatile.core.exceptions.TransformException;
import fr.ign.voxatile.core.utils.coordinates.EnvelopeProvider;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

/**
 * Data provider reading images from generic XYZ URL endpoints.
 */
public class XYZTilesProvider implements Provider<FloatGeographicDataMatrix2d> {

    /**
     * Coordinate reference system for the provider (EPSG:4326).
     */
    private static final CoordinateReferenceSystem CRS;

    static {
        String code = "EPSG:4326";
        try {
            CRS = org.geotools.referencing.CRS.decode(code, true);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private final String url;
    private final int zoom;
    private final IntToDoubleFunction decoder;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code XYZTilesProvider}.
     *
     * @param url URL with {@code {z}}, {@code {x}}, and {@code {y}} placeholders
     * @param zoom zoom level for the tiles
     * @param decoder the elevation decoder format for the tiles
     * @param envelopeProvider the provider used to compute the envelope for the given CRS and bounding box
     */
    public XYZTilesProvider(
            String url,
            int zoom,
            IntToDoubleFunction decoder,
            EnvelopeProvider envelopeProvider
        ) {
        this.url = url.replace("{z}", String.valueOf(zoom));
        this.zoom = zoom;
        this.decoder = decoder;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Result<FloatGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(CRS, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        int minX = lon2tileX(envelope.getMinX(), zoom);
        int minY = lat2tileY(envelope.getMaxY(), zoom);
        int maxX = lon2tileX(envelope.getMaxX(), zoom);
        int maxY = lat2tileY(envelope.getMinY(), zoom);

        List<FloatGeographicDataMatrix2d> tiles = new ArrayList<>();
        for (int y = minY; y <= maxY; y++)
            for (int x = minX; x <= maxX; x++) {
                BufferedImage image;
                try (InputStream stream = URI.create(url.replace("{x}", String.valueOf(x))
                                                        .replace("{y}", String.valueOf(y))).toURL().openStream()) {
                    image = ImageIO.read(stream);
                } catch (IOException e) {
                    throw new GenerationFailedException(e);
                }

                if (image != null) {
                    double tileMinX = tile2lon(x, zoom);
                    double tileMaxY = tile2lat(y, zoom);
                    double cellSizeX = (tile2lon(x + 1, zoom) - tileMinX) / (image.getWidth() - 1);
                    double cellSizeY = (tileMaxY - tile2lat(y + 1, zoom)) / (image.getHeight() - 1);
                    double offsetX = tileMinX + (cellSizeX / 2.0);
                    double offsetY = tileMaxY - (cellSizeY / 2.0);

                    tiles.add(new FloatBufferedImageMatrix(image, decoder, cellSizeX, cellSizeY, offsetX, offsetY));
                }
            }

        return new SimpleResult<>(CRS, tiles.iterator());
    }

    /*
        the formulas below are based on the Slippy map tilenames convention
            https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Common_programming_languages
    */
    private double tile2lon(int x, int z) {
        return x * 360.0 / (1 << z) - 180.0;
    }

    private int lon2tileX(double lon, int z) {
        return (int) ((lon + 180.0) / 360.0 * (1 << z));
    }

    private double tile2lat(int y, int z) {
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2.0 * y / (1 << z)))));
    }

    private int lat2tileY(double lat, int z) {
        double latRad = Math.toRadians(lat);
        return (int) ((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2 * (1 << z));
    }
}
