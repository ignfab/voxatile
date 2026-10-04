package fr.ign.voxatile.core.parameters.processors;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.FloatGeographicDataMatrix2d;
import fr.ign.voxatile.core.models.FloatMatrixModel;
import fr.ign.voxatile.core.processors.FloatMatrixProcessor;
import fr.ign.voxatile.core.processors.Processor;

/**
 * Parameters for float matrix processors.
 */
public class FloatMatrixProcessorParams extends ProcessorParams {
    @Override
    public Processor<FloatGeographicDataMatrix2d, FloatMatrixModel> create(Generation generation) {
        return new FloatMatrixProcessor(generation::makeCoordsConverter);
    }
}
