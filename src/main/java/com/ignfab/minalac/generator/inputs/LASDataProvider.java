package com.ignfab.minalac.generator.inputs;

import java.io.File;
import java.util.Iterator;

import com.github.mreutegg.laszip4j.CloseablePointIterable;
import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;
import com.github.mreutegg.laszip4j.LASReader;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;

public class LASDataProvider implements Provider<LASPointAndHeader> {
    private final File file;
    private final ReferencedEnvelope envelope;

    public LASDataProvider(File file, ReferencedEnvelope envelope) {
        this.file = file;
        this.envelope = envelope;
    }

    @Override
    public Class<LASPointAndHeader> providedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Result<LASPointAndHeader> provide() throws GenerationFailedException, RetryableException {
        LASReader reader = new LASReader(file);
        reader.insideRectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
        CloseablePointIterable points = reader.getCloseablePoints();
        LASHeader header = reader.getHeader();
        Iterator<LASPoint> iterator = points.iterator();
        return new SimpleResult<>(
            envelope.getCoordinateReferenceSystem(),
            Iterators.remap(iterator, point -> new LASPointAndHeader(point, header, !iterator.hasNext())),
            points
        );
    }
}
