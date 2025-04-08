package com.ignfab.minalac.generator.processors.post;

import java.util.ArrayList;
import java.util.List;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor applying other post-processors in sequence.
 * @param <M1> The accepted model type. Must match the one of the first most-restricting underlying post-processor.
 * @param <M2> The processed model type. Must match the one of the last underlying post-processor after going through all of them.
 */
public class SequentialPostProcessor<M1 extends Model, M2 extends Model> implements PostProcessor<M1, M2> {
    private final List<PostProcessor<Model, ?>> postProcessors;

    /**
     * Creates a new {@code SequentialPostProcessor}.
     * @param postProcessors The underlying processors to apply
     */
    public SequentialPostProcessor(List<? extends PostProcessor<?, ?>> postProcessors) {
        this.postProcessors = new ArrayList<>(postProcessors.size());
        for (PostProcessor<?, ?> postProcessor : postProcessors) {
            @SuppressWarnings("unchecked") // The model types will be validated later
            PostProcessor<Model, ?> uncheckedPostProcessor = (PostProcessor<Model, ?>) postProcessor;
            this.postProcessors.add(uncheckedPostProcessor);
        }
    }

    @Override
    public Class<? extends M2> checkProcessingType(Class<? extends Model> inputModelType) throws IllegalArgumentException {
        Class<? extends Model> modelType = inputModelType;
        for (PostProcessor<Model, ?> postProcessor : postProcessors)
            modelType = postProcessor.checkProcessingType(modelType);
        @SuppressWarnings("unchecked") // The processed model type should match the generic M2
        Class<? extends M2> processedModelType = (Class<? extends M2>) modelType;
        return processedModelType;
    }

    @Override
    public M2 process(M1 inputModel) throws IgnorableException, GenerationFailedException {
        Model model = inputModel;
        for (PostProcessor<Model, ?> postProcessor : postProcessors) {
            if (model == null)
                break;
            model = postProcessor.process(model);
        }
        @SuppressWarnings("unchecked") // The processed model type should match the generic M2
        M2 processedModel = (M2) model;
        return processedModel;
    }
}
