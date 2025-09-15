package com.ignfab.minalac.generator.parameters.models;

import com.ignfab.minalac.generator.models.ModelSelection;

/**
 * A {@code ModelSelectionParams} for testing purposes.
 */
public class TestingModelSelectionParams extends ModelSelectionParams {
    /**
     * An invalid testing model selection.
     */
    public static final TestingModelSelectionParams INVALID = new TestingModelSelectionParams(false);
    /**
     * A valid testing model selection.
     */
    public static final TestingModelSelectionParams VALID = new TestingModelSelectionParams(true);

    private final boolean valid;

    public TestingModelSelectionParams(boolean valid) {
        super();
        this.valid = valid;
    }

    @Override
    public void validate() {
        if (!valid) throw new IllegalArgumentException();
    }

    @Override
    public ModelSelection create() {
        return null;
    }
}
