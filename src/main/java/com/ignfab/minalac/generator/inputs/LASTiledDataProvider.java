package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LASTiledDataProvider implements Provider<LASPointAndHeader> {
    private final File tilesDirectory;
    private final String tileFilenameTemplate;
    private final double tileSize;
    private final double tileOffsetX;
    private final double tileOffsetY;
    private final ReferencedEnvelope envelope;

    public LASTiledDataProvider(File tilesDirectory, String tileFilenameTemplate, double tileSize, double tileOffsetX, double tileOffsetY, ReferencedEnvelope envelope) {
        this.tilesDirectory = tilesDirectory;
        this.tileFilenameTemplate = tileFilenameTemplate;
        this.tileSize = tileSize;
        this.tileOffsetX = tileOffsetX;
        this.tileOffsetY = tileOffsetY;
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
        int minX = (int) Math.floor((envelope.getMinX() + tileOffsetX) / tileSize);
        int minY = (int) Math.floor((envelope.getMinY() + tileOffsetY) / tileSize);
        int maxX = (int) Math.floor((envelope.getMaxX() + tileOffsetX) / tileSize);
        int maxY = (int) Math.floor((envelope.getMaxY() + tileOffsetY) / tileSize);
        List<Result<LASPointAndHeader>> results = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                results.add(new LASDataProvider(new File(tilesDirectory, tileFilenameTemplate.formatted(x, y)), envelope).provide());
        return new MergeResults(results);
    }

    public record MergeResults(List<Result<LASPointAndHeader>> results) implements Result<LASPointAndHeader> {
        @Override
        public void close() throws IOException {
            for (Result<LASPointAndHeader> result : results)
                result.close();
        }

        @Override
        public Iterator<LASPointAndHeader> iterator() {
            return Iterators.unwrapIterables(results.iterator());
        }
    }
}
