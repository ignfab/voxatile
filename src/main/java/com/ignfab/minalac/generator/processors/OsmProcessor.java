package com.ignfab.minalac.generator.processors;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.geometry.GeometryBuilder;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.OsmData;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Processor transforming {@code OsmData} into {@link JTSGeometryModel}.
 * It also copies OSM element tags into model's metadata.
 * <p>
 * This processor pairs well with {@link com.ignfab.minalac.generator.inputs.OverpassProvider}.
 */
public class OsmProcessor implements Processor<OsmData, JTSGeometryModel> {
    private final MapToWorldConverter converter;
    private static final GeometryBuilder GEOMETRY_BUILDER = new GeometryBuilder();

    /**
     * Creates a new {@code OsmProcessor} using the given converter.
     * @param converter the converter to transform coordinates from map to world (should use {@link OsmData#CRS} as source CRS)
     */
    public OsmProcessor(MapToWorldConverter converter) {
        this.converter = converter;
    }

    @Override
    public Class<OsmData> acceptedType() {
        return OsmData.class;
    }

    @Override
    public Class<JTSGeometryModel> modelType() {
        return JTSGeometryModel.class;
    }

    @Override
    public JTSGeometryModel process(OsmData data) throws GenerationFailedException {
        OsmEntity entity = data.entity();

        // Try to convert OSM geometry to JTS geometry
        Geometry geometry;

        try {
            geometry = switch (entity.getType()) {
                case Node -> GEOMETRY_BUILDER.build((OsmNode) entity);
                case Way -> GEOMETRY_BUILDER.build((OsmWay) entity, data.resolver());
                case Relation -> GEOMETRY_BUILDER.build((OsmRelation) entity, data.resolver());
            };
        } catch (EntityNotFoundException e) {
            throw new GenerationFailedException(e);
        }

        // Coordinates are inverted between OSM and EPSG-4326
        geometry.apply((Coordinate coordinate) -> {
            double buffer = coordinate.x;
            coordinate.x = coordinate.y;
            coordinate.y = buffer;
        });

        JTSGeometryModel model;

        try {
            model = new JTSGeometryModel(geometry, converter);
        } catch (TransformException e) {
            throw new GenerationFailedException(e);
        }

        // Copy OSM tags to model metadata
        for (int index = 0; index < entity.getNumberOfTags(); index++) {
            OsmTag tag = entity.getTag(index);

            model.setMetadata(tag.getKey(), tag.getValue());
        }
        return model;
    }

    @Override
    public void initialize(CoordinateReferenceSystem layerCrs) throws GenerationFailedException {
        if (!layerCrs.equals(OsmData.CRS))
            throw new GenerationFailedException("OSM layers should always use %s CRS, got %s.".formatted(OsmData.CRS.getName(), layerCrs.getName()));
    }

}
