package com.ignfab.minalac.generator.inputs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.Rounding;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for Web Map Service (raster data).
 */
public class WMSFloatBilDataProvider implements Provider<FloatGeographicDataMatrix2d> {
    private static final String SERVICE = "WMS";
    private static final String VERSION = "1.3.0";

    private final ParameterizedURL baseURL;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final String srsName;

    /**
     * Creates a new {@code WMSDataProvider}.
     *
     * @param baseURL base URL of the service
     * @param layer name of the WMS layer to query
     * @param crs coordinate reference system to use for this source
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public WMSFloatBilDataProvider(String baseURL, String layer, CoordinateReferenceSystem crs, EnvelopeProvider envelopeProvider) {
        this.crs = crs;
        this.envelopeProvider = envelopeProvider;

        srsName = CRS.toSRS(crs);
        if (srsName == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");

        this.baseURL = ParameterizedURL.base(baseURL)
            .parameter("SERVICE", SERVICE)
            .parameter("VERSION", VERSION)
            .parameter("REQUEST", "GetMap")
            .parameter("LAYERS", layer)
            .parameter("FORMAT", "image/x-bil;bits=32")
            .parameter("STYLES", "")
            .build();
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Result<FloatGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {

        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        // Pixel size in map units
        // TODO: Should be computed from capabilities and voxel size in realworld
        // (we don't need information more accurate than voxel size neither information more
        // accurate than capabilities)
        double pixelSize = 1;

        // This is the WMS bbox expressed in map coordinates.
        // It is used below to deduce matrix offset and cell size.
        // WMS matrix is aligned in the same way in all tiles (use of floor/ceil).
        // This prevents glitches between tiles.

        // We need margin for interpolation (-1/+1 expressed in pixelSize)
        // TODO: Margin size should come from processor (may be with PR#123?)
        double minX = Rounding.floor(envelope.getMinX(), pixelSize, -1);
        double minY = Rounding.floor(envelope.getMinY(), pixelSize, -1);
        double maxX = Rounding.ceil(envelope.getMaxX(), pixelSize, 1);
        double maxY = Rounding.ceil(envelope.getMaxY(), pixelSize, 1);

        // Formulas give integer numbers, we round them to avoid surprises with floating points
        int width  = (int) Math.round((maxX - minX) / pixelSize);
        int height = (int) Math.round((maxY - minY) / pixelSize);

        // Perform WMS query
        ParameterizedURL url = baseURL.builder()
            .parameter("CRS", srsName)
            .parameter("BBOX", minX + "," + minY + "," + maxX + "," + maxY)
            .parameter("WIDTH", width)
            .parameter("HEIGHT", height)
            .build();
        InputStream inputStream;
        try {
            inputStream = url.toURL().openStream();
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        // Read resulting binary data
        int size = 4 * width * height;
        int total = 0;

        byte[] data = new byte[size];
        try {
            int read;
            while (0 < (read = inputStream.read(data, total, size - total)))
                total = total + read;
        } catch (IOException e) {
            throw new RetryableException("Error fetching data", e);
        }

        try {
            inputStream.close();
        } catch (IOException ignored) {}

        if (total != size)
            throw new RetryableException("Incomplete data read from stream");

        FloatArrayGeographicDataMatrix2d result = new FloatArrayGeographicDataMatrix2d(width, height, minX, minY, pixelSize, pixelSize);

        // Decode binary data into float matrix
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(result.data());

        return new SimpleResult<>(crs, Iterators.iterator(result));
    }
}
