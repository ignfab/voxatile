package com.ignfab.minalac.generator.tasks;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

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
    private final int maxTries;
    private final Duration retryDelay;

    /**
     * Creates a new {@code FetchDataTask}.
     *
     * @param modelType name of type to be associated with them
     * @param provider data provider
     * @param processor processor converting provided data to models
     * @param postProcessor post-processor to run on created models
     * @param maxTries maximum number of tries
     * @param retryDelay retry delay in seconds
     */
    public FetchDataTask(
        String modelType,
        Provider<?> provider,
        Processor<?, ? extends Model> processor,
        PostProcessor<?, ?> postProcessor,
        int maxTries,
        Duration retryDelay
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
        this.maxTries = maxTries;
        this.retryDelay = retryDelay;
    }

    private void tryOnce(GenerationTile tile) throws RetryableException {
        // Resulting list of models. Will be added to store only if fetch succeeds
        List<Model> models = new LinkedList<>();

        try (Provider.Result<?> result = provider.provide(tile.limits())) {
            processor.initialize(result.crs());
            while (result.hasNext()) {
                Object data = result.next();
                try {
                    Model model = processor.process(data);
                    if (model != null) {
                        model = postProcessor.process(model);
                        if (model != null)
                            models.add(model);
                    }
                } catch (IgnorableException e) {
                    // TODO Add an exception handling policy
                    // To fail even on ignorable exceptions:
                    // throw new GenerationFailedException(e);
                }
            }
        } catch (IgnorableException e) {
            // TODO Handle this according to the policy
            // This can be thrown if unable to close a Provider.Result
            throw new RuntimeException(e);
        } catch (GenerationFailedException e) {
            throw new RuntimeException(e);
        }

        tile.models().add(modelType, models);
    }

    @Override
    public void run(GenerationTile tile) {
        String taskName = Thread.currentThread().getName();
        List<RetryableException> suppressedRetryableExceptions = new LinkedList<>();
        int tries = 0;

        do {
            try {
                tryOnce(tile);

                // Successful task
                return;

            } catch (RetryableException e) {
                suppressedRetryableExceptions.add(e);

                tries++;
                if (tries >= maxTries) {
                    System.err.printf("%s task failed (try %d of %d), giving up.%n", taskName, tries, maxTries);
                    RuntimeException exception = new RuntimeException("%s task failed after %d tries".formatted(taskName, maxTries));
                    suppressedRetryableExceptions.forEach(exception::addSuppressed);
                    throw exception;
                }

                System.out.printf("%s task failed (try %d of %d), retrying in %d seconds.%n", taskName, tries, maxTries, retryDelay.toSeconds());

                try {
                    Thread.sleep(retryDelay.toMillis());
                } catch (InterruptedException e2) {
                    throw new RuntimeException(e2);
                }
            }

        // loop exits on successful return or if max number of tries exceeded
        } while (true);
    }
}
