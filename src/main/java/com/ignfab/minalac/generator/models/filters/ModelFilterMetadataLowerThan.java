package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model filter that selects models where the metadata value is less than a specified threshold.
 */
// TODO: Rename this class to "ModelFilterMetadataCompare" and add a "greaterThan" attribute to support both lower and greater comparisons
public class ModelFilterMetadataLowerThan implements Predicate<Model> {

    private final String metadata;
    private final double lowerThan;

    /**
     * Create a new {@code ModelFilterMetadataLowerThan}.
     *
     * @param metadata name of the metadata to test
     * @param lowerThan value that the metadata must be less than
     */
    public ModelFilterMetadataLowerThan(String metadata, double lowerThan) {
        this.metadata = metadata;
        this.lowerThan = lowerThan;
    }

    @Override
    public boolean test(Model model) {
        Object obj = model.getMetadata(metadata);
        return obj instanceof Number number && number.doubleValue() < lowerThan;
    }
}
