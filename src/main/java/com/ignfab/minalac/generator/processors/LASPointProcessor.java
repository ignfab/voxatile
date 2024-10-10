package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASPointModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;

/**
 * Processor transforming {@link LASPointAndHeader LAS points} into {@link LASPointModel LAS point models}.
 * Each point becomes a model, making this processor unsuitable for large amount of points.
 * @see LASPointMergedModelProcessor
 */
public class LASPointProcessor extends ConvertingProcessor<LASPointAndHeader, LASPointModel> {
    /**
     * Creates a new {@code LASPointProcessor}.
     * @param converterProvider the converter provider to transform coordinates from map to world
     */
    public LASPointProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
    }

    @Override
    public Class<LASPointAndHeader> acceptedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Class<LASPointModel> modelType() {
        return LASPointModel.class;
    }

    @Override
    public LASPointModel process(LASPointAndHeader object) throws GenerationFailedException, IgnorableException {
        try {
            return new LASPointModel(object.header(), object.point(), converter);
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
    }
}
