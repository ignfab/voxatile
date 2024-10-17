package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;

public class JTSGeometryBufferPostProcessor implements PostProcessor<JTSGeometryModel, JTSGeometryModel> {
    private final double buffer;

    public JTSGeometryBufferPostProcessor(double buffer) {
        this.buffer = buffer;
    }

    @Override
    public Class<? super JTSGeometryModel> acceptedModelType() {
        return JTSGeometryModel.class;
    }

    @Override
    public Class<? extends JTSGeometryModel> processedModelType(Class<? extends JTSGeometryModel> inputModelType) {
        return inputModelType;
    }

    @Override
    public JTSGeometryModel process(JTSGeometryModel model) throws GenerationFailedException, IgnorableException {
        if (buffer != 0)
            model.setGeometry(model.getGeometry().buffer(buffer));
        return model;
    }
}
