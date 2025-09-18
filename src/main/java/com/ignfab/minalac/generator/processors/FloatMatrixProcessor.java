package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming {@link FloatGeographicDataMatrix2d} into a
 * {@link FloatMatrixModel}.
 */
public class FloatMatrixProcessor extends ConvertingProcessor<FloatGeographicDataMatrix2d, FloatMatrixModel> {
    /**
     * Creates a new {@code FloatMatrixProcessor}.
     * @param converterProvider converter provider to transform coordinates from map to world
     */
    public FloatMatrixProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
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
