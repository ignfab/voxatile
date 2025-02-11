package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.FeatureType;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

/**
 * Data provider using WFS 1.1 and GML 3.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider implements Provider<SimpleFeature> {
    private static final String SERVICE = "WFS";
    private static final String VERSION = "2.0.0";

    private final String baseURL;
    private final String type;
    private final ReferencedEnvelope envelope;

    /**
     * Constructs a new {@code WFS1_1_GML3_1_DataProvider}.
     *
     * @param baseURL the base URL
     * @param type Name of the WFS feature type to query
     * @param envelope Limit of area to fetch
     */
    public WFS1_1_GML3_1_DataProvider(String baseURL, String type, ReferencedEnvelope envelope) {
        this.baseURL = baseURL;
        this.type = type;
        this.envelope = envelope;
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide() throws GenerationFailedException, RetryableException {

        String srsname = CRS.toSRS(envelope.getCoordinateReferenceSystem());
        if (srsname == null)
            throw new GenerationFailedException("Could not retrieve SRS name for layer");

        URLBuilder url = new URLBuilder(baseURL);
        url.addQueryParameter("SERVICE", SERVICE);
        url.addQueryParameter("VERSION", VERSION);
        url.addQueryParameter("REQUEST", "GetFeature");
        url.addQueryParameter("OUTPUTFORMAT", "GML3");
        url.addQueryParameter("TYPENAMES", type);
        url.addQueryParameter("SRSNAME", srsname);
        url.addQueryParameter("BBOX", envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY() + "," + srsname);
        url.addQueryParameter("STARTINDEX", 0);
        url.addQueryParameter("COUNT", 1000);

        InputStream stream;
        try {
            stream = url.toURL().openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots
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
        InputStream replacedStream = new ByteArrayInputStream(string.getBytes());
        try {
            // This is the "clean but not working" (see below) way to do things:
            // return new FeaturesResult(new GML(GML.Version.GML3).decodeFeatureCollection(replacedStream).features(), replacedStream);

            // This is the "dirty but working" way:
            return new FeaturesResult(decodeFeatureCollection(replacedStream).features(), replacedStream);
        } catch (IOException e) {
            throw new RetryableException("Error fetching data", e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new GenerationFailedException("Unable to decode features", e);
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

    private record FeaturesResult(SimpleFeatureIterator features, InputStream stream) implements Result<SimpleFeature> {
        @Override
        public Iterator<SimpleFeature> iterator() {
            return new Iter();
        }

        @Override
        public void close() throws IOException {
            features.close();
            stream.close();
        }

        private final class Iter implements Iterator<SimpleFeature> {
            @Override
            public boolean hasNext() {
                return features.hasNext();
            }

            @Override
            public SimpleFeature next() {
                return features.next();
            }
        }
    }

    @Override
    public CoordinateReferenceSystem crs() {
        return envelope.getCoordinateReferenceSystem();
    }
}
