package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A data source with a provider, a processor and some (or no) post-processors.
 */
public class DataSource {
    private final ModelStore modelStore;
    private final String modelType;
    private final Provider<?> provider;
    private final Processor<Object, ?> processor;
    private final List<PostProcessor<Model, ?>> postProcessors;

    /**
     * Creates a new data source.
     *
     * @param modelStore where resulting models shoud be stored
     * @param modelType name of type to be associated with them
     * @param provider data provider
     * @param processor processor converting provided data to models
     * @param postProcessors eventual post-processor to run on created models
     */
    public DataSource(
        ModelStore modelStore,
        String modelType,
        Provider<?> provider,
        Processor<?, ? extends Model> processor,
        List<PostProcessor<?, ?>> postProcessors
    ) {
        Class<? extends Model> modelClass = processor.modelType();

        if (!processor.acceptedType().isAssignableFrom(provider.providedType()))
            throw new IllegalArgumentException("Processor cannot treat provided type. Provided = %s, Accepted = %s".formatted(provider.providedType(), processor.acceptedType()));

        this.postProcessors = new ArrayList<>();
        for (PostProcessor<?, ?> postProcessor : postProcessors) {
            if (!postProcessor.acceptedModelType().isAssignableFrom(modelClass))
                throw new IllegalArgumentException("PostProcessor cannot treat model type. Current model type = %s, Accepted model type = %s".formatted(modelType, postProcessor.acceptedModelType()));
            @SuppressWarnings("unchecked") // The model type has been validated above
            PostProcessor<Model, ?> uncheckedPostProcessor = (PostProcessor<Model, ?>) postProcessor;
            this.postProcessors.add(uncheckedPostProcessor);
            modelClass = uncheckedPostProcessor.processedModelType(modelClass);
        }

        @SuppressWarnings("unchecked")
        Processor<Object, ?> uncheckedProcessor = (Processor<Object, ?>) processor;

        this.modelStore = modelStore;
        this.modelType = modelType;
        this.provider = provider;
        this.processor = uncheckedProcessor;
    }

    /**
     * Fetches data from provider, and create and process models.
     */
    public void fetch() {
        try (Provider.Result<?> result = provider.provide()) {
            processor.initialize(result.crs());
            while (result.hasNext()) {
                Object data = result.next();
                try {
                    Model model = processor.process(data);
                    for (PostProcessor<Model, ?> postProcessor : postProcessors) {
                        if (model == null)
                            break;
                        model = postProcessor.process(model);
                    }
                    if (model != null)
                        modelStore.add(modelType, model);
                } catch (IgnorableException e) {
                    // TODO Add an exception handling policy
                    // To fail even on ignorable exceptions:
                    // throw e;
                }
            }
        } catch (RetryableException e) {
            // TODO Implement a retry mechanism
            throw new RuntimeException(e);
        } catch (IOException | GenerationFailedException e) {
            throw new RuntimeException(e);
        }
    }
}
