package fr.ign.voxatile.core.parameters.processors;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.IntegerGeographicDataMatrix2d;
import fr.ign.voxatile.core.models.FloatMatrixModel;
import fr.ign.voxatile.core.processors.Image2FloatMatrixProcessor;
import fr.ign.voxatile.core.processors.Processor;
import java.util.function.IntToDoubleFunction;

/**
 * Parameters for {@link Image2FloatMatrixProcessor}.
 */
public class Image2FloatMatrixProcessorParams extends ProcessorParams {

    private final IntToDoubleFunction decoder;

    /**
     * Creates a new {@code Image2FloatMatrixProcessorParams} with the specified decoder.
     *
     * @param decoder the function to decode integer values to double
     */
    public Image2FloatMatrixProcessorParams(IntToDoubleFunction decoder) {
        this.decoder = decoder;
    }

    @Override
    public Processor<IntegerGeographicDataMatrix2d, FloatMatrixModel> create(Generation generation) {
        return new Image2FloatMatrixProcessor(decoder, generation::makeCoordsConverter);
    }
}
