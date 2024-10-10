package com.ignfab.minalac.generator.inputs;

import com.github.mreutegg.laszip4j.CloseablePointIterable;
import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;
import com.github.mreutegg.laszip4j.LASReader;
import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.io.File;
import java.util.Iterator;

public class LASDataProvider implements Provider<LASPointAndHeader> {
    private final File file;
    private final ReferencedEnvelope envelope;

    public LASDataProvider(File file, ReferencedEnvelope envelope) {
        this.file = file;
        this.envelope = envelope;
    }

    @Override
    public CoordinateReferenceSystem crs() {
        return envelope.getCoordinateReferenceSystem();
    }

    @Override
    public Class<LASPointAndHeader> providedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Result<LASPointAndHeader> provide() throws GenerationFailedException, RetryableException {
        LASReader reader = new LASReader(file);
        reader.insideRectangle(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
        return new LASResult(reader.getCloseablePoints(), reader.getHeader());
    }

    private record LASResult(CloseablePointIterable points, LASHeader header) implements Result<LASPointAndHeader> {
        @Override
        public void close() {
            points.close();
        }

        @Override
        public Iterator<LASPointAndHeader> iterator() {
            Iterator<LASPoint> iterator = points.iterator();
            return Iterators.remap(iterator, point -> new LASPointAndHeader(point, header, !iterator.hasNext()));
        }
    }
}
