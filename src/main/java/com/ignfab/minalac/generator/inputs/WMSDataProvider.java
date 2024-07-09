package com.ignfab.minalac.generator.inputs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;

/**
 * Data provider for Web Map Service (raster data).
 */
public class WMSDataProvider {
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
    public WMSDataProvider(String baseURL, CoordinateReferenceSystem crs, Envelope envelope) {
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

    /**
     * Gets the downloaded float matrix.
     *
     * @return downloaded float matrix.
     */
    public float[] getFloatMatrix() throws IOException {
        int size = 4 * width * height;

        InputStream inputStream = new URL(baseURL
            + "&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES="
            + "&CRS=" + CRS.toSRS(crs) // TODO: Check we have a SRS!
            + "&BBOX=" + envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY()
            + "&WIDTH=" + width +  "&HEIGHT=" + height
            ).openStream();

        byte[] data = new byte[size];

        int total = 0;
        int read;
        while (0 < (read = inputStream.read(data, total, size - total)))
            total = total + read;
        if (total != size)
            throw new RuntimeException("Incomplete data read from stream");

        float[] result = new float[width * height];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(result);
        return result;
    }

    /**
     * Returns data width.
     *
     * @return data width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns data height.
     *
     * @return data height
     */
    public int getHeight() {
        return height;
    }
}
