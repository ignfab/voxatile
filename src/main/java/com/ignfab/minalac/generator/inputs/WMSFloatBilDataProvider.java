package com.ignfab.minalac.generator.inputs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;

/**
 * Data provider for Web Map Service (raster data).
 */
public class WMSFloatBilDataProvider implements Provider<FloatGeographicDataMatrix2d> {
    private final String baseURL;
    private final CoordinateReferenceSystem crs;
    private final Envelope envelope;
    private final int width;
    private final int height;

    /**
     * Creates a new {@code WMSDataProvider}.
     *
     * @param baseURL base URL of the service (should include {@code service=} parameter but no other)
     * @param crs Coordinate reference system to be used for data downloading and processing
     * @param envelope Limit of area to fetch
     */
    public WMSFloatBilDataProvider(String baseURL, CoordinateReferenceSystem crs, Envelope envelope) {
        this.baseURL = baseURL;
        this.crs = crs;
        this.envelope = envelope;

        // Let's say we want heightmap with 1 map unit precision.
        // TODO: Should computed from capabilities and voxel size in realworld
        // (we don't need information more accurate than voxel size neither information more
        // accurate than capabilities)
        this.width  = (int) Math.round(envelope.getMaxX() - envelope.getMinX());
        this.height = (int) Math.round(envelope.getMaxY() - envelope.getMinY());
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Provider.Result<FloatGeographicDataMatrix2d> provide() throws GenerationFailedException, RetryableException {
        int size = 4 * width * height;

        InputStream inputStream;
        try {
            inputStream = new URL(baseURL
                + "&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES="
                + "&CRS=" + CRS.toSRS(crs) // TODO: Check we have a SRS!
                + "&BBOX=" + envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY()
                + "&WIDTH=" + width +  "&HEIGHT=" + height
                ).openStream();
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Shouldn't have happened", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        byte[] data = new byte[size];

        int total = 0;
        int read;
        try {
            while (0 < (read = inputStream.read(data, total, size - total)))
                total = total + read;
        } catch (IOException e) {
            throw new RetryableException("Error fetching data", e);
        }

        if (total != size)
            throw new RetryableException("Incomplete data read from stream");

        FloatGeographicDataMatrix2d result = new FloatGeographicDataMatrix2d(
            width,
            height,
            envelope.getMinX(),
            envelope.getMinY(),
            envelope.getWidth() / width,
            envelope.getHeight() / height);

        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(result.data());
        return new Result(result);
    }

    /**
     * Result returned by provide method. Here, only one result is provided.
     *
     * @param data resulting data
     */
    public record Result(FloatGeographicDataMatrix2d data) implements Provider.Result<FloatGeographicDataMatrix2d> {
        @Override
        public Iterator<FloatGeographicDataMatrix2d> iterator() {
            return Iterators.iterator(data);
        }

        @Override
        public void close() throws IOException {}
    }

}
