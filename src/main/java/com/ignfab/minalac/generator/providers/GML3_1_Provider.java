package com.ignfab.minalac.generator.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gml2.FeatureTypeCache;
import org.geotools.xsd.Parser;
import org.geotools.xsd.impl.ParserHandler.ContextCustomizer;
import org.locationtech.jts.geom.Geometry;
import org.picocontainer.MutablePicoContainer;
import org.xml.sax.SAXException;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Provider decoding data from GML 3.1 format.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "GML 3.1"
public class GML3_1_Provider implements Provider<SimpleFeature> {
    private final Fetcher fetcher;

    /**
     * Constructs a new {@code GML3_1_Provider}.
     *
     * @param fetcher fetcher to use to get GML-encoded data
     */
    public GML3_1_Provider(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        return new GMLResult(fetcher.fetch(bbox));
    }

    private static class GMLResult implements Result<SimpleFeature> {
        private final Fetcher.FetchResult fetchResult;
        private final CoordinateReferenceSystem crs;
        private SimpleFeatureIterator iterator = null;
        private SimpleFeature forcedNext = null;

        GMLResult(Fetcher.FetchResult fetchResult) throws GenerationFailedException, RetryableException {
            this.fetchResult = fetchResult;
            crs = fetchResult.crs() == null ? findCRS() : fetchResult.crs();
        }

        private CoordinateReferenceSystem findCRS() throws GenerationFailedException, RetryableException {
            if (hasNext()) {
                forcedNext = next();
                if (forcedNext.getDefaultGeometry() instanceof Geometry geom && geom.getUserData() instanceof CoordinateReferenceSystem geomCrs)
                    return geomCrs;
            }
            throw new GenerationFailedException("Unable to retrieve CRS");
        }

        @Override
        public CoordinateReferenceSystem crs() {
           return crs;
       }

        private void closeCurrentIterator() {
            if (iterator != null) {
                iterator.close();
                iterator = null;
            }
        }

        private void nextIterator() throws GenerationFailedException, RetryableException {
            closeCurrentIterator();
            if (!fetchResult.hasNext())
                return;

            try (InputStream stream = fetchResult.next()) {
                // This is the "clean but not working" (see below) way to do things:
                // SimpleFeatureCollection collection = new GML(GML.Version.GML3).decodeFeatureCollection(stream);

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
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            if (forcedNext != null)
                return true;

            if (iterator == null || !iterator.hasNext())
                nextIterator();

            return iterator != null && iterator.hasNext();
        }

        @Override
        public SimpleFeature next() throws GenerationFailedException, RetryableException {
            if (forcedNext != null) {
                SimpleFeature next = forcedNext;
                forcedNext = null;
                return next;
            }

            if (!hasNext())
                throw new NoSuchElementException("No more elements!");

            return iterator.next();
        }

        @Override
        public void close() {
            closeCurrentIterator();
            forcedNext = null;
        }
    }

    ///////////////////////////
    // Hacky part starts here
    //
    // GeoTools has a problem with variable feature types (feature type = set of available metadata).
    // WFS from IGN could send various feature types (ie not with the same metadata set) in a response to a query.
    //
    // Using GML::decodeFeatureCollection with a single parameter (the input stream) will take the first feature
    // as template for all feature types. So if a metadata is absent from this first feature, it will be removed
    // from all features of the response set.
    //
    // Using GML::decodeFeatureCollection with an additional parameter set to `true` is supposed to build a type
    // covering all encountered feature metadata (if a metadata appears once, it will be set for all features).
    // But this gives an exception about "retyping" features.
    //
    // Actually, we'll be totally ok to have features with various types. Post-processing mechanism will be
    // able to handle that correctly. The solution here has been to copy and simplify decodeFeatureCollection so
    // it will return features with their type as in XML.

    /**
     * This is a hack to circumvent GeoTools problem with variable type features.
     * <p>
     * This version of SimpleFeatureCollection will return features with heterogeneous set of metadata.
     * <p>
     * For simplicity, it has been narrowed down to SimpleFeatureCollection and SimpleFeature cases
     * and will not work if parser returns something else.
     *
     * @param in Input stream providing GML data
     * @return Collection of features
     */
    private static SimpleFeatureCollection decodeFeatureCollection(InputStream in)
        throws IOException, SAXException, ParserConfigurationException {
        Parser parser = new Parser(new org.geotools.gml3.GMLConfiguration());
        parser.setContextCustomizer(new NoFeatureTypeCacheCustomizer());
        Object obj = parser.parse(in);
        // Here we suppose obj to be a SimpleFeatureCollection or a SimpleFeature
        // but there may be other cases (see `toFeatureCollection` from `GML`)
        if (obj instanceof SimpleFeatureCollection collection) {
            return collection;
        }
        if (obj instanceof SimpleFeature feature) {
            return DataUtilities.collection(feature);
        }
        throw new ClassCastException("Unexpected " + obj.getClass() + " produced from GML data");
    }

    /**
     * A feature type cache customizer that does no caching.
     * <p>
     * If lack of type caching causes performances issue, {@code DynamicFeatureTypeCacheCustomizer}
     * from {@code GML} could be used instead (as it is private, it will have to be copied here).
     */
    private static final class NoFeatureTypeCacheCustomizer implements ContextCustomizer {
        @Override
        public void customizeContext(MutablePicoContainer context) {
            Object instance = context.getComponentInstanceOfType(FeatureTypeCache.class);
            context.unregisterComponentByInstance(instance);
            // This disables feature type caching
            context.registerComponentInstance(new FeatureTypeCache() {
                @Override
                public void put(FeatureType type) {}
            });
        }
    }

    //
    // Hacky part ends here
    ////////////////////////
}
