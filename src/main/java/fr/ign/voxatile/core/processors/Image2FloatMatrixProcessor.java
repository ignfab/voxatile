package fr.ign.voxatile.core.processors;

import java.util.function.IntToDoubleFunction;

import fr.ign.voxatile.core.exceptions.IgnorableException;
import fr.ign.voxatile.core.inputs.FloatArrayGeographicDataMatrix2d;
import fr.ign.voxatile.core.inputs.IntegerGeographicDataMatrix2d;
import fr.ign.voxatile.core.models.FloatMatrixModel;
import fr.ign.voxatile.core.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming {@link IntegerGeographicDataMatrix2d} into a
 * {@link FloatMatrixModel}.
 */
public class Image2FloatMatrixProcessor extends ConvertingProcessor<IntegerGeographicDataMatrix2d, FloatMatrixModel> {

    private final IntToDoubleFunction decoder;

    /**
     * Create a new {@code Image2FloatMatrixProcessor}.
     *
     * @param decoder the function to decode integer image values to double values
     * @param converterProvider the provider for coordinate converters
     */
    public Image2FloatMatrixProcessor(IntToDoubleFunction decoder, CoordsConverterProvider converterProvider) {
        super(converterProvider);
        this.decoder = decoder;
    }

    @Override
    public Class<IntegerGeographicDataMatrix2d> acceptedType() {
        return IntegerGeographicDataMatrix2d.class;
    }

    @Override
    public Class<FloatMatrixModel> modelType() {
        return FloatMatrixModel.class;
    }

    @Override
    public FloatMatrixModel process(IntegerGeographicDataMatrix2d matrix) throws IgnorableException {
        try {
            int sizeX = matrix.sizeX();
            int sizeY = matrix.sizeY();
            float[] data = new float[sizeX * sizeY];

            for (int y = 0; y < sizeY; y++) {
                int rowOffset = y * sizeX;
                for (int x = 0; x < sizeX; x++) {
                    data[rowOffset + x] = (float) decoder.applyAsDouble(matrix.getInteger(x, y));
                }
            }

            FloatArrayGeographicDataMatrix2d floatMatrix = new FloatArrayGeographicDataMatrix2d(
                data,
                matrix.sizeX(),
                matrix.sizeY(),
                matrix.offsetX(),
                matrix.offsetY(),
                matrix.cellSizeX(),
                matrix.cellSizeY()
            );
            return new FloatMatrixModel(floatMatrix, converter);
        } catch (Exception e) {
            throw new IgnorableException("Failed to process image to float matrix", e);
        }
    }
}
