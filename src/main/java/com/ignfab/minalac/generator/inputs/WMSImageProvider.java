package com.ignfab.minalac.generator.inputs;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class WMSImageProvider implements Provider<IntegerGeographicDataMatrix2d> {
    private static final String SERVICE = "WMS";
    private static final String VERSION = "1.3.0";

    private final ParameterizedURL baseURL;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final String srsName;

    ImageInputStream stream;

    public WMSImageProvider(String baseURL, String layer, String format, CoordinateReferenceSystem crs, EnvelopeProvider envelopeProvider) {
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
            .parameter("FORMAT", format)
            .parameter("STYLES", "")
            .build();
    }

    @Override
    public Class<IntegerGeographicDataMatrix2d> providedType() {
        return IntegerGeographicDataMatrix2d.class;
    }

    @Override
    public Provider.Result<IntegerGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {

        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

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
            System.out.println(url.toURL());
            inputStream = url.toURL().openStream();
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RetryableException("Error decoding data", e);
        }

        try {
            inputStream.close();
        } catch (IOException ignored) {}

        return new SimpleResult<>(envelope.getCoordinateReferenceSystem(), Iterators.iterator(
            new BufferedImageGeographicDataMatrix2d(
                image,
                envelope.getMinX(),
                envelope.getMinY(),
                envelope.getWidth() / width,
                envelope.getHeight() / height)
            )
        );
    }
}
