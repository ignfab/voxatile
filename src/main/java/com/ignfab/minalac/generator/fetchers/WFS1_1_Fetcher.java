package com.ignfab.minalac.generator.fetchers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Fetcher using WFS 1.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1"
public class WFS1_1_Fetcher implements Fetcher {
    private static final String SERVICE = "WFS";
    private static final String VERSION = "2.0.0";

    private final ParameterizedURL url;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final int maxFeaturePerQuery;
    private final String srsName;

    /**
     * Constructs a new {@code WFS1_1_Fetcher}.
     *
     * @param baseURL the base URL
     * @param type Name of the WFS feature type to query
     * @param crs coordinate reference system to use for this source
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     * @param maxFeaturePerQuery Maximum number of feature per query
     *
     * @throws IllegalArgumentException if SRS name could not be retrieved from envelope.
     */
    public WFS1_1_Fetcher(String baseURL, String type, CoordinateReferenceSystem crs, EnvelopeProvider envelopeProvider, int maxFeaturePerQuery) {
        this.maxFeaturePerQuery = maxFeaturePerQuery;
        this.crs = crs;
        this.envelopeProvider = envelopeProvider;

        srsName = CRS.toSRS(crs);
        if (srsName == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");

        url = ParameterizedURL.base(baseURL)
            .parameter("SERVICE", SERVICE)
            .parameter("VERSION", VERSION)
            .parameter("REQUEST", "GetFeature")
            .parameter("OUTPUTFORMAT", "gml3")
            .parameter("TYPENAMES", type)
            .parameter("SRSNAME", srsName)
            .build();
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException {
        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        ParameterizedURL url = this.url.builder()
            .parameter("BBOX", envelope.getMinX()
                + "," + envelope.getMinY()
                + "," + envelope.getMaxX()
                + "," + envelope.getMaxY()
                + "," + srsName)
            .build();

        // First we need to know total feature count
        int count;
        InputStream stream;
        try {
            stream = url.builder()
                .parameter("resultType", "hits")
                .buildURL().openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        GetHitsHandler handler = new GetHitsHandler();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(stream, handler);
            count = handler.getCount();
        } catch (SAXException | IOException e) {
            throw new RetryableException(e);
        } catch (ParserConfigurationException e) {
            throw new GenerationFailedException(e);
        }

        // Then we give hand to `WFSResult` class for the rest.
        return new WFSResult(url, count, maxFeaturePerQuery);
    }

    private static class WFSResult implements FetchResult {
        private final ParameterizedURL url;
        private final int total;
        private final int count;
        private int startIndex;

        WFSResult(ParameterizedURL url, int total, int maxFeaturePerQuery) {
            this.url = url;
            this.total = total;
            count = maxFeaturePerQuery;
            startIndex = 0;
        }

        @Override
        public InputStream next() throws RetryableException, GenerationFailedException {
            if (startIndex >= total)
                throw new NoSuchElementException("No more elements!");

            InputStream stream;

            try {
                stream = url.builder()
                    .parameter("STARTINDEX", startIndex)
                    .parameter("COUNT", count)
                    .buildURL().openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots
            } catch (MalformedURLException e) {
                throw new GenerationFailedException("Invalid URL for layer", e);
            } catch (IOException e) {
                throw new RetryableException("Error opening WFS connection", e);
            }

            startIndex += count;

            return stream;
        }

        @Override
        public boolean hasNext() {
            return startIndex < total;
        }
    }

    private static final class GetHitsHandler extends DefaultHandler {
        private int count = -1;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            try {
                count = Integer.parseInt(attributes.getValue("numberMatched"));
            } catch (NumberFormatException e) {
                throw new SAXException("Attribute \"numberMatched\" is not a number.", e);
            }
        }

        public int getCount() throws SAXException {
            if (count == -1)
                throw new SAXException("Attribute \"numberMatched\" not found.");
            return count;
        }
    }
}
