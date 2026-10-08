package fr.ign.voxatile.core.inputs;

import java.io.File;
import java.io.IOException;

import org.eclipse.imagen.iterator.RandomIter;
import org.eclipse.imagen.iterator.RandomIterFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.InvalidGridGeometryException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.Hints;

import fr.ign.voxatile.core.exceptions.GenerationFailedException;
import fr.ign.voxatile.core.exceptions.RetryableException;
import fr.ign.voxatile.core.exceptions.TransformException;
import fr.ign.voxatile.core.utils.coordinates.EnvelopeProvider;
import fr.ign.voxatile.core.utils.iterator.Iterators;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

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
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox).intersection(grid.getGridGeometry().getEnvelope2D());
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        // Compute in grid envelope (pixel envelope)
        GridEnvelope2D gridEnvelope;
        try {
            gridEnvelope = grid.getGridGeometry().worldToGrid(envelope);
        } catch (InvalidGridGeometryException | org.geotools.api.referencing.operation.TransformException e) {
           throw new GenerationFailedException(e);
        }

        // This will ensure we have enough pixels for interpolation
        // - First, we need to be sure we always have four samples around each voxel center.
        //   I.E: we need to be sure we always include outer samples
        //   gridEnvelope only includes a sample if original envelope overlaps its pixel.
        //   this means outer sample is only included if envelope limit is closer to them than to inner sample.
        // - Then, we have to fix envelope computation which excludes last line/column.
        // We could do that more accurately. Growing by 2 pixel in every direction will work but may include one or two useless pixel columns or lines.
        // TODO: grown envelope may overflow out of raster data. Is this correct?
        gridEnvelope.grow(2, 2);

        // Now we reconvert actual grid envelope to map coordinates
        // This will be used to consistently adjust pixel positions to map coordinates
        ReferencedEnvelope mapEnvelope;

        try {
            mapEnvelope = grid.getGridGeometry().gridToWorld(gridEnvelope);
        } catch (org.geotools.api.referencing.operation.TransformException e) {
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
            mapEnvelope.getMinX(),
            mapEnvelope.getMinY(),
            mapEnvelope.getWidth() / gridEnvelope.width,
            mapEnvelope.getHeight() / gridEnvelope.height
        );

        return new SimpleResult<>(crs, Iterators.iterator(result));
    }
}
