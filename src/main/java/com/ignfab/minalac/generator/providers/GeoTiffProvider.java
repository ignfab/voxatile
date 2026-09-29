package com.ignfab.minalac.generator.providers;

import java.io.IOException;

import org.eclipse.imagen.iterator.RandomIter;
import org.eclipse.imagen.iterator.RandomIterFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.Hints;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.geodata.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.geodata.FloatImageGeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for GeoTiff files (raster data).
 */
public class GeoTiffProvider implements Provider<FloatGeographicDataMatrix2d> {
    private final Fetcher fetcher;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code GeoTiffProvider}.
     *
     * @param fetcher the fetcher for GeoTiff data
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public GeoTiffProvider(Fetcher fetcher, EnvelopeProvider envelopeProvider) {
        this.fetcher = fetcher;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Provider.Result<FloatGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        Fetcher.FetchResult result = fetcher.fetch(bbox);

        // This hint must remain enabled
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, true);
        if (result.crsHint() != null)
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, result.crsHint());

        return new GeoTiffResult(result, hints, bbox);
    }

    private class GeoTiffResult implements Result<FloatGeographicDataMatrix2d> {
        private final Fetcher.FetchResult fetchResult;
        private final Hints hints;
        private final WorldBBox3d bbox;
        private final CoordinateReferenceSystem crs;
        private GridCoverage2D prefetched = null;

        private GeoTiffResult(Fetcher.FetchResult fetchResult, Hints hints, WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
            this.fetchResult = fetchResult;
            this.hints = hints;
            this.bbox = bbox;
            crs = findCRS(fetchResult.crsHint());
        }

        private CoordinateReferenceSystem findCRS(CoordinateReferenceSystem crsHint) throws GenerationFailedException, RetryableException {
            if (crsHint != null)
                return crsHint;
            if (hasNext()) {
                prefetched = fetch();
                return prefetched.getCoordinateReferenceSystem();
            }
            throw new GenerationFailedException("Unable to retrieve CRS");
        }

        @Override
        public CoordinateReferenceSystem crs() {
            return crs;
        }

        @Override
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            return prefetched != null || fetchResult.hasNext();
        }

        private GridCoverage2D fetch() throws GenerationFailedException, RetryableException {
            if (prefetched != null) {
                GridCoverage2D fetched = prefetched;
                prefetched = null;
                return fetched;
            }

            try {
                return new GeoTiffReader(fetchResult.next(), hints).read();
            } catch (IOException e) {
                throw new RetryableException(e);
            }
        }

        @Override
        public FloatGeographicDataMatrix2d next() throws GenerationFailedException, RetryableException {
            GridCoverage2D grid = fetch();
            CoordinateReferenceSystem crs = grid.getCoordinateReferenceSystem();
            if (crs != this.crs)
                throw new GenerationFailedException("Mixed-CRS data returned from fetcher");

            ReferencedEnvelope envelope;
            GridEnvelope2D gridEnvelope;
            try {
                envelope = envelopeProvider.computeForCRS(crs, bbox).intersection(grid.getGridGeometry().getEnvelope2D());
                gridEnvelope = grid.getGridGeometry().worldToGrid(envelope);
            } catch (FactoryException | TransformException | org.geotools.api.referencing.operation.TransformException e) {
                throw new GenerationFailedException(e);
            }

            // RandomIter provides a view of the underlying image to read arbitrary pixel values
            RandomIter data = RandomIterFactory.create(grid.getRenderedImage(), gridEnvelope);

            return new FloatImageGeographicDataMatrix2d(
                data,
                gridEnvelope.x,
                gridEnvelope.y,
                gridEnvelope.width,
                gridEnvelope.height,
                envelope.getMinX(),
                envelope.getMinY(),
                envelope.getWidth() / gridEnvelope.width,
                envelope.getHeight() / gridEnvelope.height
            );
        }

        @Override
        public void close() {
            // TODO Do we have something to close? Should/could input streams be closed sooner?
        }
    }
}
