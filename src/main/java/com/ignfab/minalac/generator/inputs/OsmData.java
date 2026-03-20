package com.ignfab.minalac.generator.inputs;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.resolve.OsmEntityProvider;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * OSM entity with a fully resolvable geometry.
 *
 * @param resolver the resolver to use to resolve references to other elements
 * @param entity the element whose geometry might contain references to other elements
 */
public record OsmData(OsmEntityProvider resolver, OsmEntity entity) {
    /**
     * Coordinate Reference System for OSM Data (EPSG:4326 / WGS-84).
     */
    public static final CoordinateReferenceSystem CRS;

    static {
        String code = "EPSG:4326";
        try {
            CRS = org.geotools.referencing.CRS.decode(code);
        } catch (FactoryException e) {
            throw new RuntimeException("Could not initialize OSM CRS to %s!".formatted(code), e);
        }
    }
}
