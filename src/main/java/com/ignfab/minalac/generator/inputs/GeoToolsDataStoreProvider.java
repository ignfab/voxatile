package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.data.Query;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Data provider using GeoTools {@link DataStore}.
 */
public abstract class GeoToolsDataStoreProvider implements Provider<SimpleFeature> {
    private final ReferencedEnvelope envelope;

    /**
     * Constructs a new {@code GeoToolsDataStoreProvider}.
     *
     * @param envelope the envelope to filter features.
     */
    public GeoToolsDataStoreProvider(ReferencedEnvelope envelope) {
        this.envelope = envelope;
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
    public CoordinateReferenceSystem crs() {
        return envelope.getCoordinateReferenceSystem();
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide() throws GenerationFailedException, RetryableException {
        try {
            DataStore store = DataStoreFinder.getDataStore(dataStoreParams());
            if (store == null)
                throw new GenerationFailedException("No DataStore found by GeoTools. Wrong params?");
            String typeName = typeName(store);

            FilterFactory ff = CommonFactoryFinder.getFilterFactory();
            FeatureType schema = store.getSchema(typeName);

            String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
            CoordinateReferenceSystem targetCRS = schema.getGeometryDescriptor().getCoordinateReferenceSystem();
            ReferencedEnvelope targetEnvelope;
            if (envelope.getCoordinateReferenceSystem().equals(targetCRS))
                targetEnvelope = envelope;
            else
                targetEnvelope = envelope.transform(targetCRS, true);

            Filter filter = ff.bbox(ff.property(geometryPropertyName), targetEnvelope);
            Query query = new Query(typeName, filter);

            FeatureReader<SimpleFeatureType, SimpleFeature> reader = store.getFeatureReader(query, Transaction.AUTO_COMMIT);
            return new FeaturesResult(reader, store);
        } catch (IOException e) {
            throw new RetryableException(e);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }
    }

    private record FeaturesResult(FeatureReader<SimpleFeatureType, SimpleFeature> reader, DataStore store) implements Result<SimpleFeature> {
        @Override
        public Iterator<SimpleFeature> iterator() {
            return new Iter();
        }

        @Override
        public void close() throws IOException {
            reader.close();
            store.dispose();
        }

        private final class Iter implements Iterator<SimpleFeature> {
            @Override
            public boolean hasNext() {
                try {
                    return reader.hasNext();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public SimpleFeature next() {
                try {
                    return reader.next();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
