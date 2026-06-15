package com.ignfab.minalac.generator.inputs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml2.FeatureTypeCache;
import org.geotools.referencing.CRS;
import org.geotools.xsd.Parser;
import org.geotools.xsd.impl.ParserHandler.ContextCustomizer;
import org.picocontainer.MutablePicoContainer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider using WFS 1.1 and GML 3.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider implements Provider<SimpleFeature> {
    private static final String SERVICE = "WFS";
    private static final String VERSION = "2.0.0";

    private final ParameterizedURL url;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final int maxFeaturePerQuery;
    private final String srsName;

    /**
     * Constructs a new {@code WFS1_1_GML3_1_DataProvider}.
     *
     * @param baseURL the base URL
     * @param type Name of the WFS feature type to query
     * @param crs coordinate reference system to use for this source
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     * @param maxFeaturePerQuery Maximum number of feature per query
     *
     * @throws IllegalArgumentException if SRS name could not be retrieved from envelope.
     */
    public WFS1_1_GML3_1_DataProvider(String baseURL, String type, CoordinateReferenceSystem crs, EnvelopeProvider envelopeProvider, int maxFeaturePerQuery, String token) {
        this.maxFeaturePerQuery = maxFeaturePerQuery;
        this.crs = crs;
        this.envelopeProvider = envelopeProvider;

        srsName = CRS.toSRS(crs);
        if (srsName == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");

        this.url = ParameterizedURL.base(baseURL)
            .parameter("token", token)
            .parameter("SERVICE", SERVICE)
            .parameter("VERSION", VERSION)
            .parameter("REQUEST", "GetFeature")
            .parameter("OUTPUTFORMAT", "gml3")
            .parameter("TYPENAMES", type)
            .parameter("SRSNAME", srsName)
            .build();
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {

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
        return new WFSResult(url, count);
    }

   /**
     * A Result class for WFS that will fetch more features when needed.
     */
    private class WFSResult implements Result<SimpleFeature> {

        private final ParameterizedURL url;
        private final int total;
        private int remaining;
        private SimpleFeatureIterator iterator;

        WFSResult(ParameterizedURL url, int total) {
            this.url = url;
            this.total = total;
            remaining = total;
            iterator = null;
        }

       @Override
       public CoordinateReferenceSystem crs() {
           return crs;
       }

        /**
         * Fetches more results from URL.
         */
        private void fetchMore() throws RetryableException, GenerationFailedException {
            InputStream stream;

            try {
                stream = url.builder()
                    .parameter("STARTINDEX", total - remaining)
                    .parameter("COUNT", Math.min(remaining, maxFeaturePerQuery))
                    .buildURL().openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots
            } catch (MalformedURLException e) {
                throw new GenerationFailedException("Invalid URL for layer", e);
            } catch (IOException e) {
                throw new RetryableException("Error opening connection", e);
            }

            // This code is temporary and no attention is given to its (bad) performance
            // TODO: Improve that!
            byte[] bytes;
            try {
                bytes = stream.readAllBytes();
                stream.close();
            } catch (IOException e) {
                throw new RetryableException("Error opening connection", e);
            }

            // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
            String string = new String(bytes).replace("http://BDTOPO_V3", "explicitly-invalid");
            stream = new ByteArrayInputStream(string.getBytes());
            try {
                // This is the "clean but not working" (see below) way to do things:
                // SimpleFeatureCollection collection = new GML(GML.Version.GML3).decodeFeatureCollection(replacedStream);

                // This is the "dirty but working" way:
                SimpleFeatureCollection collection = decodeFeatureCollection(stream);

                iterator = collection.features();
            } catch (IOException e) {
                throw new RetryableException("Error fetching data", e);
            } catch (ParserConfigurationException | SAXException e) {
                throw new GenerationFailedException("Unable to decode features", e);
            }
        }

        @Override
        public void close() {
            if (iterator != null)
                iterator.close();
        }

        // Checks we are in a valid position, eventually move.
        private void check() throws RetryableException, GenerationFailedException {
            if (remaining == 0)
                return;

            if (iterator == null || !iterator.hasNext())
                fetchMore();
        }

        @Override
        public boolean hasNext() throws RetryableException, GenerationFailedException {
            check();

            return iterator != null && iterator.hasNext();
        }

        @Override
        public SimpleFeature next() throws RetryableException, GenerationFailedException {
            check();

            if (remaining == 0)
                throw new NoSuchElementException("No more elements!");

            remaining--;

            try {
                return iterator.next();
            } catch (NoSuchElementException e) {
                throw new RetryableException("Could not fetch all expected features", e);
            }
        }
    }

    /**
     * Sax handler retrieving "numberMatched" attribute.
     */
    private static final class GetHitsHandler extends DefaultHandler {
        private int count = -1;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            count = Integer.parseInt(attributes.getValue("numberMatched"));
        }

        public int getCount() throws SAXException {
            if (count == -1)
                throw new SAXException("Attribute \"numberMatched\" not found.");
            return count;
        }
    }

    ///////////////////////////
    // Hacky part starts here
    //
    // Geotools has a problem with variable feature types (feature type = set of available metadata).
    // WFS from IGN could send various feature types (ie not with the same metadata set) in a response to a query.
    //
    // Using GML::decodeFeatureCollection with a single parameter (the input stream) will take the first feature
    // as template for all feature types. So if a metadata is absent from this first feature, it will be removed
    // from all features of the response set.
    //
    // Using GML::decodeFeatureCollection with an aditional parameter set to `true` is supposed to build a type
    // covering all encountered feature metadata (if a metadata appears once, it will be set for all features).
    // But this gives an exception about "retyping" features.
    //
    // Actually, we'll be totally ok to have features with various types. Post-processing mechanism will be
    // able to handle that correctly. The solution here has been to copy and simplify decodeFeatureCollection so
    // it will return features with their type as in XML.

    /**
     * This is a hack to circumvent Geotools problem with variable type features.
     *
     * This version of SimpleFeatureCollection will return features with heterogeneous set of metadata.
     *
     * For simplicity, it has been narrowed down to SimpleFeatureCollection and SimpleFeature cases
     * and will not work if parser returns something else.
     *
     * @param in Input stream providing WFS XML
     * @return Collection of features
     */
    private SimpleFeatureCollection decodeFeatureCollection(InputStream in)
        throws IOException, SAXException, ParserConfigurationException {
        Parser parser = new Parser(new org.geotools.gml3.GMLConfiguration());
        parser.setContextCustomizer(new NoFeatureTypeCacheCustomizer());
        Object obj = parser.parse(in);
        // Here we suppose obj to be a SimpleFeatureCollection or a SimpleFeature
        // but there may be other cases (see `toFeatureCollection` from `GML.class`)
        if (obj instanceof SimpleFeatureCollection collection) {
            return collection;
        }
        if (obj instanceof SimpleFeature feature) {
            return DataUtilities.collection(feature);
        }
        throw new ClassCastException("Unexpected " + obj.getClass() + " produced from WFS data");
    }

    /**
     * A feature type cache customizer that does no caching.
     *
     * If lack of type caching causes performances issue, `DynamicFeatureTypeCacheCustomizer` from `GML.class`
     * could be used instead (as it is private, it will have to be copied here).
     */
    private static final class NoFeatureTypeCacheCustomizer implements ContextCustomizer {
        @Override
        public void customizeContext(MutablePicoContainer context) {
            Object instance = context.getComponentInstanceOfType(FeatureTypeCache.class);
            context.unregisterComponentByInstance(instance);
            // This disables feature type caching
            context.registerComponentInstance(new FeatureTypeCache() {
                @Override
                public void put(FeatureType type) { }
            });
        }
    }

    //
    // Hacky part ends here
    ////////////////////////
}
