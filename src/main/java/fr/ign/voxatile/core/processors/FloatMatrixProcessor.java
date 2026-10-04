package fr.ign.voxatile.core.processors;

import fr.ign.voxatile.core.exceptions.IgnorableException;
import fr.ign.voxatile.core.exceptions.TransformException;
import fr.ign.voxatile.core.inputs.FloatGeographicDataMatrix2d;
import fr.ign.voxatile.core.models.FloatMatrixModel;
import fr.ign.voxatile.core.utils.coordinates.CoordsConverterProvider;

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
