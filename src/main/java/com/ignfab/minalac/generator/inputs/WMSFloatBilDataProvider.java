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

        // Let's say we want heightmap with 1 map unit precision.
        // TODO: Should computed from capabilities and voxel size in realworld
        // (we don't need information more accurate than voxel size neither information more
        // accurate than capabilities)
        int width  = (int) Math.round(envelope.getMaxX() - envelope.getMinX());
        int height = (int) Math.round(envelope.getMaxY() - envelope.getMinY());

        ParameterizedURL url = baseURL.builder()
            .parameter("CRS", srsName)
            .parameter("BBOX", envelope.getMinX()
                + "," + envelope.getMinY()
                + "," + envelope.getMaxX()
                + "," + envelope.getMaxY())
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

        FloatArrayGeographicDataMatrix2d result = new FloatArrayGeographicDataMatrix2d(
            width,
            height,
            envelope.getMinX(),
            envelope.getMinY(),
            envelope.getWidth() / width,
            envelope.getHeight() / height
        );

        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(result.data());

        return new SimpleResult<>(crs, Iterators.iterator(result));
    }
}
