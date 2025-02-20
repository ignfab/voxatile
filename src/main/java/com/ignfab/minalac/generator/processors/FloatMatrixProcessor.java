package com.ignfab.minalac.generator.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Processor transforming {@link FloatGeographicDataMatrix2d} into a
 * {@link FloatMatrixModel}.
 */
public class FloatMatrixProcessor implements Processor<FloatGeographicDataMatrix2d, FloatMatrixModel> {
    private final CoordsConverterProvider converterProvider;
    private MapToWorldConverter converter;

    /**
     * Creates a new {@code FloatMatrixProcessor}.
     * @param converterProvider converter provider to transform coordinates from map to world
     */
    public FloatMatrixProcessor(CoordsConverterProvider converterProvider) {
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
    public Class<FloatGeographicDataMatrix2d> acceptedType() {
        return FloatGeographicDataMatrix2d.class;
    }

    @Override
    public Class<FloatMatrixModel> modelType() {
        return FloatMatrixModel.class;
    }

    @Override
    public FloatMatrixModel process(FloatGeographicDataMatrix2d matrix) throws IgnorableException {
        try {
            return new FloatMatrixModel(matrix, converter);
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
    }
}
