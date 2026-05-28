package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor copying a metadata into another, effectively
 * adding a new one with a new name and the same value.
 * This operation can be seen as renaming a metadata, with
 * the side effect of leaving the original one in place.
 * <p>
 * The same model object is returned after post-processing.
 */
// TODO once model values can handle any type of values, this won't be necessary anymore
public class MetadataCopyPostProcessor extends PostProcessor.Generic {
    private final String from;
    private final String to;
    private final boolean abortIfFromIsAbsent;
    private final boolean doNotOverwriteExistingTo;

    /**
     * Creates a new post-processor copying {@code from} into {@code to}.
     * @param from the original name of the metadata to copy
     * @param to the destination name to store the copied metadata
     * @param abortIfFromIsAbsent whether to abort if metadata is absent
     * @param doNotOverwriteExistingTo whether to abort if copy would overwrite existing metadata
     */
    public MetadataCopyPostProcessor(String from, String to, boolean abortIfFromIsAbsent, boolean doNotOverwriteExistingTo) {
        this.from = from;
        this.to = to;
        this.abortIfFromIsAbsent = abortIfFromIsAbsent;
        this.doNotOverwriteExistingTo = doNotOverwriteExistingTo;
    }

    @Override
    public Model process(Model model) {
        if (doNotOverwriteExistingTo && model.hasMetadata(to))
            return model;
        if (model.hasMetadata(from))
            model.setMetadata(to, model.getMetadata(from));
        else if (!abortIfFromIsAbsent)
            model.setMetadata(to, null);
        return model;
    }
}
