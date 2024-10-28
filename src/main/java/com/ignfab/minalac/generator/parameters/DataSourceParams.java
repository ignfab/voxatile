package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.DataSource;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.PostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.ProviderParams;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Represents the parameters used for {@link DataSource} creation.
 */
// Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DataSourceParams {
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
    public List<PostProcessorParams> postProcessors = new ArrayList<>();

    /**
     * Dependencies (optional).
     */
    public List<String> after;

   /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType type to give to resulting models
     * @param provider data provider for this source
     * @param processor data processor for provided data
     */
    @ConstructorProperties({"modelType", "provider", "processor"})
    public DataSourceParams(String modelType, ProviderParams provider, ProcessorParams processor) {
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
        for (PostProcessorParams params : postProcessors)
            params.validate();
    }

    /**
     * Creates the corresponding {@code DataSource}.
     *
     * @param generation the generation context
     * @return the created data source
     */
    public DataSource create(Generation generation) {
        Provider<?> provider = this.provider.create(generation);
        Processor<?, ?> processor = this.processor.create(generation, provider.crs());
        List<PostProcessor<?, ?>> postProcessors = new ArrayList<>();
        for (PostProcessorParams params : this.postProcessors)
            postProcessors.add(params.create());

        return new DataSource(
            generation.models(),
            modelType,
            provider,
            processor,
            postProcessors
        );
    }
}
