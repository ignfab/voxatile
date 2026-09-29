package com.ignfab.minalac.generator.fetchers;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.geodata.OsmData;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for Overpass.
 */
public class OverpassFetcher extends HttpFetcher {
    private final String query;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code OverpassProvider}.
     *
     * @param url Overpass URL
     * @param query Overpass query. Each element will be processed as a separate model.
     * @param envelopeProvider the envelope provider to filter elements.
     */

    public OverpassFetcher(
        HttpInit init,
        String baseURL,
        String query,
        EnvelopeProvider envelopeProvider
    ) {
        super(init, ParameterizedURL.base(baseURL).build());
        this.query = query;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException {
        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(OsmData.CRS, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        // Fetch data from query, within bbox, including sub-ways and nodes but getting rid of eventual tags.
        // (we will rely on tags to distinguish wanted data from constituting ways and nodes)
        String data = String.format(Locale.US, "[bbox:%f,%f,%f,%f];%s;out;>;out skel;",
            envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY(), query);

        String body = "data=" + URLEncoder.encode(data, StandardCharsets.UTF_8);

        HttpResponse<InputStream> response;
        try {
            response = executePost(baseURL.toURI(), body);
        } catch (URISyntaxException e) {
            throw new GenerationFailedException("Invalid URL", e);
        }
        return new OverpassResult(response.body());
    }

    private class OverpassResult implements FetchResult {
        private final InputStream body;
        private boolean next = true;

        OverpassResult(InputStream body) {
            this.body = body;
        }

        @Override
        public CoordinateReferenceSystem crsHint() {
            return OsmData.CRS;
        }

        @Override
        public InputStream next() throws RetryableException, GenerationFailedException {
            if (!next)
                throw new NoSuchElementException();
            next = false;
            return body;
        }

        @Override
        public boolean hasNext() {
            return next;
        }
    }
}
