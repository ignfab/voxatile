package com.ignfab.minalac.generator.parameters.tasks.tile;

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
