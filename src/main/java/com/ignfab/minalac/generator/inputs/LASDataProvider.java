package com.ignfab.minalac.generator.inputs;

import java.io.File;

import com.github.mreutegg.laszip4j.CloseablePointIterable;
import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASReader;
import com.github.mreutegg.laszip4j.LASVariableLengthRecord;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for LAS/LAZ files (LiDAR data).
 */
public class LASDataProvider implements Provider<LASPointAndHeader> {
    private final File file;
    private final CoordinateReferenceSystem crsOverride;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code LASDataProvider}.
     * @param file the LAS/LAZ file
     * @param crsOverride the CRS to use regardless of the one found in data
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public LASDataProvider(File file, CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        this.file = file;
        this.crsOverride = crsOverride;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<LASPointAndHeader> providedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Result<LASPointAndHeader> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        LASReader reader = new LASReader(file);
        LASHeader header = reader.getHeader();
        CoordinateReferenceSystem crs;
        ReferencedEnvelope envelope;
        try {
            crs = crsOverride == null ? findCRS(header) : crsOverride;
            if (crs == null)
                throw new GenerationFailedException("Could not find CRS inside LAS/LAZ file, and no override given");
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }
        reader.insideRectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
        CloseablePointIterable points = reader.getCloseablePoints();

        return new SimpleResult<>(
            crs,
            Iterators.remap(points.iterator(), point -> new LASPointAndHeader(point, header)),
            points
        );
    }

    /* package-private */ static CoordinateReferenceSystem findCRS(LASHeader header) throws FactoryException {
        for (LASVariableLengthRecord vlr : header.getVariableLengthRecords())
            if (vlr.getUserID().equals("LASF_Projection") && vlr.getRecordID() == 2112)
                // This looks really weird but it works.
                // I can't tell if this is because the CRS in my test files is bad or this is really required...
                return CRS.decode(CRS.toSRS(CRS.parseWKT(vlr.getDataAsString())));
        return null;
    }
}
