package com.ignfab.minalac.generator.processors;

import java.awt.image.BufferedImage;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.inputs.FloatArrayGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming raw Terrarium images into a queryable elevation model.
 */
public class TerrariumImageProcessor extends ConvertingProcessor<BufferedImage, FloatMatrixModel> {
    // private MapToWorldConverter converter;

    public TerrariumImageProcessor(CoordsConverterProvider converterFactory) {
        super(converterFactory);
    }

    @Override
    public Class<BufferedImage> acceptedType() {
        return BufferedImage.class;
    }

    @Override
    public Class<FloatMatrixModel> modelType() {
        return FloatMatrixModel.class;
    }

    @Override
    public FloatMatrixModel process(BufferedImage image) throws GenerationFailedException, IgnorableException {
        if (image == null)
            throw new IgnorableException("Image is null, ignoring this element");

        int width = image.getWidth();
        int height = image.getHeight();

        FloatArrayGeographicDataMatrix2d matrix = new FloatArrayGeographicDataMatrix2d(
            width, height, 0, 0, 1.0, 1.0
        );
        
        float[] data = matrix.data();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                float elevation = ((rgb >> 16) & 0xFF) * 256f + ((rgb >> 8) & 0xFF) + (rgb & 0xFF) / 256f - 32768f;
                data[y * width + x] = elevation;
            }

        try {
            return new FloatMatrixModel(matrix, converter);
        } catch (Exception e) {
            throw new GenerationFailedException("Failed to create FloatMatrixModel", e);
        }
    }
}
