package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.operation.TransformException;
import org.locationtech.jts.geom.Geometry;

/**
 * Processor transforming {@link SimpleFeature} GeoTools object
 * into {@link JTSGeometryModel}.
 * It also copies feature's properties inside model's metadata.
 * <p>
 * This processor pairs well with {@link com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider}.
 */
public class GeoToolsVectorProcessor implements Processor<SimpleFeature, JTSGeometryModel> {
    private final CoordsConverter converter;

    /**
     * Creates a new processor using the given converter to for the {@link JTSGeometryModel}.
     * @param converter the converter to pass to the geometry model
     */
    public GeoToolsVectorProcessor(CoordsConverter converter) {
        this.converter = converter;
    }

    @Override
    public Class<SimpleFeature> acceptedType() {
        return SimpleFeature.class;
    }

    @Override
    public Class<JTSGeometryModel> modelType() {
        return JTSGeometryModel.class;
    }

    @Override
    public JTSGeometryModel process(SimpleFeature feature) throws IgnorableException {
        try {
            JTSGeometryModel model = new JTSGeometryModel((Geometry) feature.getDefaultGeometry(), converter);
            for (Property property : feature.getProperties())
                model.setMetadata(property.getName().getLocalPart(), property.getValue());
            return model;
        } catch (TransformException e) {
            throw new IgnorableException("Unable to create model: Failed to transform JTS geometry", e);
        }
    }
}
