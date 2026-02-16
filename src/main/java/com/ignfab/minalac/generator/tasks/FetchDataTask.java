package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * A {@link TileTask} fetching data from a provider, processing models with a processor applying a post-processor to each model.
 */
public class FetchDataTask implements TileTask {
    private final String modelType;
    private final Provider<?> provider;
    private final Processor<Object, ?> processor;
    private final PostProcessor<Model, ?> postProcessor;

    /**
     * Creates a new {@code FetchDataTask}.
     *
     * @param modelType name of type to be associated with them
     * @param provider data provider
     * @param processor processor converting provided data to models
     * @param postProcessor post-processor to run on created models
     */
    public FetchDataTask(
        String modelType,
        Provider<?> provider,
        Processor<?, ? extends Model> processor,
        PostProcessor<?, ?> postProcessor
    ) {
        if (!processor.acceptedType().isAssignableFrom(provider.providedType()))
            throw new IllegalArgumentException("%s cannot treat provided type. Provided = %s, Accepted = %s".formatted(
                processor.getClass().getSimpleName(),
                provider.providedType(),
                processor.acceptedType()
            ));
        postProcessor.checkProcessingType(processor.modelType());

        @SuppressWarnings("unchecked") // The provided type has been validated above
        Processor<Object, ?> uncheckedProcessor = (Processor<Object, ?>) processor;
        @SuppressWarnings("unchecked") // The model type has been validated above
        PostProcessor<Model, ?> uncheckedPostProcessor = (PostProcessor<Model, ?>) postProcessor;

        this.modelType = modelType;
        this.provider = provider;
        this.processor = uncheckedProcessor;
        this.postProcessor = uncheckedPostProcessor;
    }

    /**
     * Fetches data from provider, and create and process models.
     *
     * @param tile tile for which data is fetched (it gives the wanted area)
     */
    public void run(GenerationTile tile) {
        try (Provider.Result<?> result = provider.provide(tile.modelTypeVolume(modelType).get())) {
            processor.initialize(result.crs());
            while (result.hasNext()) {
                Object data = result.next();
                try {
                    Model model = processor.process(data);
                    if (model != null) {
                        model = postProcessor.process(model);
                        if (model != null)
                            tile.models().add(modelType, model);
                    }
                } catch (IgnorableException e) {
                    // TODO Add an exception handling policy
                    // To fail even on ignorable exceptions:
                    // throw new GenerationFailedException(e);
                }
            }
        } catch (RetryableException e) {
            // TODO Implement a retry mechanism
            throw new RuntimeException(e);
        } catch (IgnorableException e) {
            // TODO Handle this according to the policy
            // This can be thrown if unable to close a Provider.Result
            throw new RuntimeException(e);
        } catch (GenerationFailedException e) {
            throw new RuntimeException(e);
        }
    }
}
