package com.ignfab.minalac.generator.inputs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.github.mreutegg.laszip4j.LASReader;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for multiple LAS/LAZ files (LiDAR data).
 * <p>
 * All tiles must have the same size (square), follow the given naming format and share the same root parent directory.
 * Unless overridden, the CRS is retrieved from an arbitrary tile.
 */
public class LASTiledDataProvider implements Provider<LASPointAndHeader> {
    private final File tilesDirectory;
    private final String tileFilenameTemplate;
    private final double tileSize;
    private final double tileOffsetX;
    private final double tileOffsetY;
    private final CoordinateReferenceSystem crsOverride;
    private final EnvelopeProvider envelopeProvider;

    /**
     * Creates a new {@code LASTiledDataProvider}.
     * @param tilesDirectory the directory containing tiles
     * @param tileFilenameTemplate the filename template of tiles
     * @param tileSize the size of tiles
     * @param tileOffsetX the x-offset of tiles
     * @param tileOffsetY the y-offset of tiles
     * @param crsOverride the CRS to use regardless of the one found in data
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public LASTiledDataProvider(
        File tilesDirectory,
        String tileFilenameTemplate,
        double tileSize,
        double tileOffsetX,
        double tileOffsetY,
        CoordinateReferenceSystem crsOverride,
        EnvelopeProvider envelopeProvider
    ) {
        this.tilesDirectory = tilesDirectory;
        this.tileFilenameTemplate = tileFilenameTemplate;
        this.tileSize = tileSize;
        this.tileOffsetX = tileOffsetX;
        this.tileOffsetY = tileOffsetY;
        this.crsOverride = crsOverride;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<LASPointAndHeader> providedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Result<LASPointAndHeader> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        CoordinateReferenceSystem crs;
        ReferencedEnvelope envelope;
        try {
            crs = crsOverride == null ? findCRS() : crsOverride;
            if (crs == null)
                throw new GenerationFailedException("Could not find CRS inside any LAS/LAZ file, and no override given");
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }
        int minX = (int) Math.floor((envelope.getMinX() + tileOffsetX) / tileSize);
        int minY = (int) Math.floor((envelope.getMinY() + tileOffsetY) / tileSize);
        int maxX = (int) Math.floor((envelope.getMaxX() + tileOffsetX) / tileSize);
        int maxY = (int) Math.floor((envelope.getMaxY() + tileOffsetY) / tileSize);
        List<Result<LASPointAndHeader>> results = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                results.add(new LASDataProvider(new File(tilesDirectory, tileFilenameTemplate.formatted(x, y)), crs, envelopeProvider).provide(bbox));
        return new MultiResult<>(crs, results);
    }

    private CoordinateReferenceSystem findCRS() throws FactoryException {
        try (Stream<Path> paths = Files.walk(tilesDirectory.toPath(), 16, FileVisitOption.FOLLOW_LINKS)) {
            return paths.filter(FileHelpers::isReadableRegularFile)
                .map(Path::toFile)
                .filter(file -> file.getName().toLowerCase().matches("^(.*)\\.la[sz]$"))
                .map(file -> {
                    try {
                        return LASDataProvider.findCRS(new LASReader(file).getHeader());
                    } catch (FactoryException e) {
                        throw new WrappedFactoryException(e);
                    }
                })
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
        } catch (WrappedFactoryException e) {
            throw e.getCause();
        } catch (IOException e) {
            return null;
        }
    }

    private static final class WrappedFactoryException extends RuntimeException {
        WrappedFactoryException(FactoryException cause) {
            super(cause);
        }

        @Override
        public FactoryException getCause() {
            return (FactoryException) super.getCause();
        }
    }
}
