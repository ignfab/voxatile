package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import org.geotools.api.data.DataStore;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Data provider using Shapefile.
 */
public class ShapefileDataProvider extends GeoToolsDataStoreProvider {
    private final File file;

    /**
     * Constructs a new {@code ShapefileDataProvider}.
     *
     * @param file the Shapefile.
     * @param crsOverride the CRS to use regardless of one found in data.
     * @param envelopeProvider the envelope provider to filter features.
     */
    public ShapefileDataProvider(File file, CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        super(crsOverride, envelopeProvider);
        this.file = file;
    }

    @Override
    protected Map<String, ?> dataStoreParams() throws GenerationFailedException {
        try {
            return Map.of(
                "url", file.toURI().toURL()
            );
        } catch (MalformedURLException e) {
            throw new GenerationFailedException(e);
        }
    }

    @Override
    protected String typeName(DataStore store) throws RetryableException {
        try {
            return store.getTypeNames()[0];
        } catch (IOException e) {
            throw new RetryableException(e);
        }
    }
}
