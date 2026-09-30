package com.ignfab.minalac.generator.fetchers;

import java.io.InputStream;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.geodata.OsmData;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for Overpass.
 */
public class OverpassFetcher implements Fetcher {
    private final OverpassPoolSpec poolSpec;
    private final int queryId;
    /**
     * Creates a new {@code OverpassProvider}.
     *
     * @param url Overpass URL
     * @param query Overpass query. Each element will be processed as a separate model.
     * @param envelopeProvider the envelope provider to filter elements.
     */

    public OverpassFetcher(
        OverpassPoolSpec poolSpec,
        String query,
        EnvelopeProvider envelopeProvider
    ) {
        this.poolSpec = poolSpec;
        queryId = poolSpec.addQuery(query);
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException {
        // TODO: we should get context from fetch arguments
        OverpassPool pool = poolSpec.getPoolForTile(GenerationTile.current());
        return new OverpassResult(pool.getResults(queryId, bbox));
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
