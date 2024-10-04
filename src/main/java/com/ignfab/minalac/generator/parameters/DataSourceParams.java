package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;
import java.util.List;

import com.ignfab.minalac.generator.generation.DataSource;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
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
     * Creates the corresponding {@code DataSource}.
     *
     * @param generation the generation context
     * @return the created data source
     */
    public DataSource create(Generation generation) {
        Provider<?> provider = this.provider.create(generation);
        Processor<?, ?> processor = this.processor.create(generation, provider.crs());

        return new DataSource(
            generation.models(),
            modelType,
            provider,
            processor,
            new PostProcessor<?, ?>[]{});
            //TODO: Manage postprocessors
    }
}
