package com.ignfab.minalac.generator.inputs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;

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
        return new MultiResult<>(envelope.getCoordinateReferenceSystem(), results);
    }
}
