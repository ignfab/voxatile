package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;

/**
 * A (supposedly) task that has a model selection.
 */
public interface HasModelSelection {
    /**
     * @return the model selection params.
     */
    ModelSelectionParams models();
}