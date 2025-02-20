package com.ignfab.minalac.generator.processors;

import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.locationtech.jts.geom.Geometry;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Processor transforming {@link SimpleFeature} GeoTools object
 * into {@link JTSGeometryModel}.
 * It also copies feature's properties inside model's metadata.
 * <p>
 * This processor pairs well with {@link com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider}.
 */
public class GeoToolsVectorProcessor implements Processor<SimpleFeature, JTSGeometryModel> {
    private final CoordsConverterProvider converterProvider;
    private MapToWorldConverter converter;

    /**
     * Creates a new processor using the given converter to for the {@link JTSGeometryModel}.
     * @param converterProvider the converter provider to transform coordinates from map to world
     */
    public GeoToolsVectorProcessor(CoordsConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    @Override
    public void initialize(CoordinateReferenceSystem layerCrs) throws GenerationFailedException {
        try {
            converter = converterProvider.computeForCRS(layerCrs);
        } catch (FactoryException e) {
            throw new GenerationFailedException("Could not find a converter from layer to generation CRS", e);
        }
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
