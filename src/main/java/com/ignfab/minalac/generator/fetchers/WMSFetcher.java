package com.ignfab.minalac.generator.fetchers;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.Rounding;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data fetcher for Web Map Service as GeoTiff (raster data).
 */
public class WMSFetcher extends HttpFetcher {
    private static final String SERVICE = "WMS";
    private static final String VERSION = "1.3.0";

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
    public WMSFetcher(HttpInit init, String baseURL, String layer, CoordinateReferenceSystem crs, EnvelopeProvider envelopeProvider) {
        super(init, ParameterizedURL.base(baseURL)
            .parameter("SERVICE", SERVICE)
            .parameter("VERSION", VERSION)
            .parameter("REQUEST", "GetMap")
            .parameter("LAYERS", layer)
            .parameter("FORMAT", "image/geotiff")
            .parameter("STYLES", "")
            .build());
        this.crs = crs;
        this.envelopeProvider = envelopeProvider;

        srsName = CRS.toSRS(crs);
        if (srsName == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
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

        try {
            return new WMSResult(baseURL.builder()
                .parameter("CRS", srsName)
                .parameter("BBOX", minX + "," + minY + "," + maxX + "," + maxY)
                .parameter("WIDTH", width)
                .parameter("HEIGHT", height)
                .buildURI());
        } catch (URISyntaxException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        }
    }

    private class WMSResult implements FetchResult {
        private final URI uri;
        private boolean hasNext = true;

        private WMSResult(URI uri) {
            this.uri = uri;
        }

        @Override
        public CoordinateReferenceSystem crsHint() {
            return crs;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public InputStream next() throws GenerationFailedException, RetryableException {
            if (!hasNext)
                throw new NoSuchElementException("No more results");
            hasNext = false;

            // Perform WMS query
            return executeGet(uri).body();
        }
    }
}
