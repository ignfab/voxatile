package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value that is always absent.
 */
public final class AbsentValue implements ModelValue {
    /**
     * Singleton instance.
     */
    public static final AbsentValue INSTANCE = new AbsentValue();

    private AbsentValue() {}

    @Override
    public Optional<Double> get(Model model) {
        return Optional.empty();
    }
}
