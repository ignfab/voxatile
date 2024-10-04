package com.ignfab.minalac.generator.inputs;

import org.geotools.api.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;

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
     * @param envelope the envelope to filter features.
     */
    public GeoPackageDataProvider(File file, String typeName, ReferencedEnvelope envelope) {
        super(envelope);
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
