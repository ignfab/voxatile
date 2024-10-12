package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.ImageGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Processor transforming {@link ImageGeographicDataMatrix2d} into an
 * {@link IntegerMatrixModel}.
 */
public class IntegerMatrixProcessor implements Processor<ImageGeographicDataMatrix2d, IntegerMatrixModel> {
    private final MapToWorldConverter converter;

    /**
     * Creates a new {@code IntegerMatrixProcessor}.
     *
     * @param converter converter to use to transform coordinates from map to world
     */
    public IntegerMatrixProcessor(MapToWorldConverter converter) {
        this.converter = converter;
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
