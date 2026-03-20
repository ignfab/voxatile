package com.ignfab.minalac.generator.processors;

import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Geometry;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming {@link SimpleFeature} GeoTools object
 * into {@link JTSGeometryModel}.
 * It also copies feature's properties inside model's metadata.
 * <p>
 * This processor pairs well with {@link com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider}.
 */
public class GeoToolsVectorProcessor extends ConvertingProcessor<SimpleFeature, JTSGeometryModel> {
    /**
     * Creates a new processor using the given converter for the {@link JTSGeometryModel}.
     * @param converterProvider the converter provider to transform coordinates from map to world
     */
    public GeoToolsVectorProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
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
