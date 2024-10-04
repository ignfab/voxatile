package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import org.geotools.api.data.DataStore;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.util.Map;

/**
 * Data provider using GeoPackage.
 */
public class GeoPackageDataProvider extends GeoToolsDataStoreProvider {
    private final File file;
    private final String typeName;

    /**
     * Constructs a new {@code GeoPackageDataProvider}.
     *
     * @param file the GPKG file.
     * @param typeName the type name to use inside the file.
     * @param crsOverride the CRS to use regardless of one found in data.
     * @param envelopeProvider the envelope provider to filter features.
     */
    public GeoPackageDataProvider(File file, String typeName, CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        super(crsOverride, envelopeProvider);
        this.file = file;
        this.typeName = typeName;
    }

    @Override
    protected Map<String, ?> dataStoreParams() {
        return Map.of(
            "dbtype", "geopkg",
            "database", file.getAbsolutePath(),
            "read-only", true
        );
    }

    @Override
    protected String typeName(DataStore store) {
        return typeName;
    }
}
