package com.ignfab.minalac.generator.inputs;

import java.io.File;
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
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for GeoTiff files (raster data).
 */
public class GeoTiffDataProvider implements Provider<FloatGeographicDataMatrix2d> {
    private final File file;
    private final CoordinateReferenceSystem crsOverride;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code GeoTiffDataProvider}.
     *
     * @param file the GeoTiff file
     * @param crsOverride the CRS to use regardless of one found in data.
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public GeoTiffDataProvider(File file, CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        this.file = file;
        this.crsOverride = crsOverride;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<FloatGeographicDataMatrix2d> providedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Provider.Result<FloatGeographicDataMatrix2d> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        GridCoverage2D grid;
        try {
            // This hint must remain enabled
            Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, true);
            if (crsOverride != null)
                hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, crsOverride);
            grid = new GeoTiffReader(file, hints).read();
        } catch (IOException e) {
            throw new RetryableException(e);
        }
        CoordinateReferenceSystem crs = grid.getCoordinateReferenceSystem();

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

        FloatGeographicDataMatrix2d result = new FloatImageGeographicDataMatrix2d(
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

        return new SimpleResult<>(crs, Iterators.iterator(result));
    }
}
