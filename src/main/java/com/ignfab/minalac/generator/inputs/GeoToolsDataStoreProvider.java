package com.ignfab.minalac.generator.inputs;

import java.io.IOException;
import java.util.Map;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.data.Query;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider using GeoTools {@link DataStore}.
 */
public abstract class GeoToolsDataStoreProvider implements Provider<SimpleFeature> {
    private final CoordinateReferenceSystem crsOverride;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Constructs a new {@code GeoToolsDataStoreProvider}.
     *
     * @param crsOverride the CRS to use regardless of one found in data.
     * @param envelopeProvider the envelope provider to filter features.
     */
    public GeoToolsDataStoreProvider(CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        this.crsOverride = crsOverride;
        this.envelopeProvider = envelopeProvider;
    }

    /**
     * Computes params for GeoTools to find the data store.
     * @return the appropriate params
     * @throws GenerationFailedException if something goes wrong
     * @see DataStoreFinder#getDataStore(Map)
     */
    protected abstract Map<String, ?> dataStoreParams() throws GenerationFailedException;

    /**
     * Computes the type name to use.
     * @param store the data store
     * @return the appropriate type name
     * @throws GenerationFailedException if something goes wrong
     * @throws RetryableException if something retryable goes wrong
     * @see DataStore#getSchema(String)
     */
    protected abstract String typeName(DataStore store) throws GenerationFailedException, RetryableException;

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        try {
            // TODO Find a better way to make sure we dispose the store in case of exception
            // (but be careful not to dispose it too early, especially before reading data!)
            DataStore store = DataStoreFinder.getDataStore(dataStoreParams());
            if (store == null)
                throw new GenerationFailedException("No DataStore found by GeoTools. Wrong params?");
            String typeName = typeName(store);
            GeometryDescriptor geom = store.getSchema(typeName).getGeometryDescriptor();

            CoordinateReferenceSystem crs = crsOverride == null ? geom.getCoordinateReferenceSystem() : crsOverride;
            ReferencedEnvelope envelope;
            try {
                envelope = envelopeProvider.computeForCRS(crs, bbox);
            } catch (FactoryException | TransformException e) {
                store.dispose();
                throw new GenerationFailedException(e);
            }

            FilterFactory ff = CommonFactoryFinder.getFilterFactory();
            Filter filter = ff.bbox(ff.property(geom.getLocalName()), envelope);
            Query query = new Query(typeName, filter);

            return new FeaturesResult(crs, store.getFeatureReader(query, Transaction.AUTO_COMMIT), store);
        } catch (IOException e) {
            throw new RetryableException(e);
        }
    }

    private record FeaturesResult(CoordinateReferenceSystem crs, FeatureReader<SimpleFeatureType, SimpleFeature> reader, DataStore store) implements Result<SimpleFeature> {
        @Override
        public void close() throws IgnorableException {
            try {
                reader.close();
            } catch (IOException e) {
                throw new IgnorableException(e);
            } finally {
                store.dispose();
            }
        }

        @Override
        public boolean hasNext() throws RetryableException {
            try {
                return reader.hasNext();
            } catch (IOException e) {
                throw new RetryableException(e);
            }
        }

        @Override
        public SimpleFeature next() throws RetryableException {
            try {
                return reader.next();
            } catch (IOException e) {
                throw new RetryableException(e);
            }
        }
    }
}
