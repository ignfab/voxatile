package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.IdentityPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.PostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.ProviderParams;
import com.ignfab.minalac.generator.tasks.FetchDataTask;

/**
 * Parameters for creating a {@link FetchDataTask}.
 */
public class FetchDataTaskParams extends TaskParams {
    /**
     * Type to give to provided models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String modelType;

    /**
     * Number of retries in case of failure (optional, default 0).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int retry = 0;

    /**
     * Delay in seconds between two retries (optional, default 10).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int retryDelay = 10;

    /**
     * Data provider (required).
     */
    public ProviderParams provider;

    /**
     * Data processor (required if provider has no default processor).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ProcessorParams processor;

    /**
     * Additional data-processing steps (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PostProcessorParams postProcessing = new IdentityPostProcessorParams();

   /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType type to give to resulting models
     * @param provider data provider for this source
     */
    @ConstructorProperties({"modelType", "provider"})
    public FetchDataTaskParams(String modelType, ProviderParams provider) {
        this.modelType = modelType;
        this.provider = provider;
        this.processor = provider.defaultProcessor();
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isBlank())
            throw new IllegalArgumentException("The 'modelType' field cannot be empty or contain only whitespace.");

        if (retry < 0)
            throw new IllegalArgumentException("`retry` must be a positive integer.");

        if (retryDelay < 0)
            throw new IllegalArgumentException("`retryDelay` must be a positive integer.");

        if (processor == null)
            throw new IllegalArgumentException("Missing processor and no default processor");

        super.validate();
        provider.validate();
        processor.validate();
        postProcessing.validate();
    }

    @Override
    public FetchDataTask create(Generation generation) {
        return new FetchDataTask(
            modelType,
            provider.create(generation),
            processor.create(generation),
            postProcessing.create(generation),
            retry + 1,
            Duration.ofSeconds(retryDelay)
        );
    }
}
