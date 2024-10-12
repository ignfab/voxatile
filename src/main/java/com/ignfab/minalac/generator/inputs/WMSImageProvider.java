package com.ignfab.minalac.generator.inputs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;

/**
 * Data provider for Web Map Service (raster data).
 */
public class WMSImageProvider implements Provider<ImageGeographicDataMatrix2d> {
    private static final String SERVICE = "WMS";
    private static final String VERSION = "1.3.0";

    private final ParameterizedURL baseURL;
    private final ReferencedEnvelope envelope;

    private final int width;
    private final int height;

    /**
     * Creates a new {@code WMSImageProvider}.
     *
     * @param baseURL base URL of the service
     * @param layer name of the WMS layer to query
     * @param envelope limit of area to fetch
     */
    public WMSImageProvider(String baseURL, String layer, ReferencedEnvelope envelope) {
        this.envelope = envelope;

        // Let's say we want heightmap with 1 map unit precision.
        // TODO: Should computed from capabilities and voxel size in realworld
        // (we don't need information more accurate than voxel size neither information more
        // accurate than capabilities)
        this.width  = (int) Math.round(envelope.getMaxX() - envelope.getMinX());
        this.height = (int) Math.round(envelope.getMaxY() - envelope.getMinY());

        String srsname = CRS.toSRS(envelope.getCoordinateReferenceSystem());
        if (srsname == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");

        this.baseURL = ParameterizedURL.base(baseURL)
                .parameter("SERVICE", SERVICE)
                .parameter("VERSION", VERSION)
                .parameter("REQUEST", "GetMap")
                .parameter("LAYERS", layer)
                .parameter("FORMAT", "image/jpeg")
                .parameter("STYLES", "")
                .parameter("CRS", srsname)
                .parameter("BBOX", envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY())
                .parameter("WIDTH", width)
                .parameter("HEIGHT", height)
                .build();
    }

    @Override
    public Class<ImageGeographicDataMatrix2d> providedType() {
        return ImageGeographicDataMatrix2d.class;
    }

    @Override
    public Provider.Result<ImageGeographicDataMatrix2d> provide() throws GenerationFailedException, RetryableException {
        InputStream inputStream;
        try {
            inputStream = baseURL.toURL().openStream();
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RetryableException("Error fetching data", e);
        }

        try {
            inputStream.close();
        } catch (IOException ignored) {}

        ImageGeographicDataMatrix2d result = new ImageGeographicDataMatrix2d(
            image,
            envelope.getMinX(),
            envelope.getMinY(),
            envelope.getWidth() / width,
            envelope.getHeight() / height
        );
        return new SimpleResult<>(envelope.getCoordinateReferenceSystem(), Iterators.iterator(result));
    }
}
