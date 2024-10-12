package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.ImageGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming {@link ImageGeographicDataMatrix2d} into an
 * {@link IntegerMatrixModel}.
 */
public class IntegerMatrixProcessor extends ConvertingProcessor<ImageGeographicDataMatrix2d, IntegerMatrixModel> {
    /**
     * Creates a new {@code IntegerMatrixProcessor}.
     *
     * @param converterProvider
     *     the converter provider to transform coordinates from map to world
     */
    public IntegerMatrixProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
    }

    @Override
    public Class<ImageGeographicDataMatrix2d> acceptedType() {
        return ImageGeographicDataMatrix2d.class;
    }

    @Override
    public Class<IntegerMatrixModel> modelType() {
        return IntegerMatrixModel.class;
    }

    @Override
    public IntegerMatrixModel process(ImageGeographicDataMatrix2d matrix) throws IgnorableException {
        try {
            return new IntegerMatrixModel(matrix, converter);
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
    }
}
