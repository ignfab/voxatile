package com.ignfab.minalac.generator.parameters.processors;

import java.awt.image.BufferedImage;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.TerrariumImageProcessor;

/**
 * Parameters for Terrarium image processors.
 */
public class TerrariumImageProcessorParams extends ProcessorParams {
@Override
    public Processor<BufferedImage, FloatMatrixModel> create(Generation generation) {
        return new TerrariumImageProcessor(crs -> {
            try {
                return generation.makeCoordsConverter(crs);
            } catch (org.geotools.api.referencing.FactoryException e) {
                throw new RuntimeException("Failed to create coordinates converter", e);
            }
        });
    }
}
