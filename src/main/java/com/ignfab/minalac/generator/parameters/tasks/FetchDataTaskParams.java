package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

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
public class FetchDataTaskParams extends TileTaskParams {
    /**
     * Type to give to provided models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String modelType;

    /**
     * Data provider (required).
     */
    public ProviderParams provider;

    /**
     * Data processor (required).
     */
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
     * @param processor data processor for provided data
     */
    @ConstructorProperties({"modelType", "provider", "processor"})
    public FetchDataTaskParams(String modelType, ProviderParams provider, ProcessorParams processor) {
        this.modelType = modelType;
        this.provider = provider;
        this.processor = processor;
    }

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {
        if (modelType.isBlank())
            throw new IllegalArgumentException("The 'modelType' field cannot be empty or contain only whitespace.");

        provider.validate();
        processor.validate();
        postProcessing.validate();
    }

    /**
     * Creates the corresponding {@code DataSource}.
     *
     * @param generation the generation context
     * @return the created data source
     */
    public FetchDataTask create(Generation generation) {
        return new FetchDataTask(
            generation.models(),
            modelType,
            provider.create(generation),
            processor.create(generation),
            postProcessing.create()
        );
    }
}
