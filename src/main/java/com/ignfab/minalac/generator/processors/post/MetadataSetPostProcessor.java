package com.ignfab.minalac.generator.processors.post;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Post-processor defining a metadata from a {@link ModelValue}.
 * <p>
 * The same model object is returned after post-processing.
 */
public class MetadataSetPostProcessor extends PostProcessor.Generic {
    private final String metadata;
    private final ModelValue value;
    private final boolean abortIfValueIsAbsent;
    private final boolean doNotOverwriteExistingMetadata;

    /**
     * Creates a new post-processor defining {@code metadata} from {@code value}.
     * @param metadata the destination name to store the copied metadata
     * @param value the value to set
     * @param abortIfValueIsAbsent whether to abort if value is absent
     * @param doNotOverwriteExistingMetadata whether to abort if it would overwrite existing metadata
     */
    public MetadataSetPostProcessor(String metadata, ModelValue value, boolean abortIfValueIsAbsent, boolean doNotOverwriteExistingMetadata) {
        this.metadata = metadata;
        this.value = value;
        this.abortIfValueIsAbsent = abortIfValueIsAbsent;
        this.doNotOverwriteExistingMetadata = doNotOverwriteExistingMetadata;
    }

    @Override
    public Model process(Model model) {
        if (doNotOverwriteExistingMetadata && model.hasMetadata(metadata))
            return model;
        Optional<Double> val = value.get(model);
        if (val.isPresent())
            model.setMetadata(metadata, val.get());
        else if (!abortIfValueIsAbsent)
            model.setMetadata(metadata, null);
        return model;
    }
}
