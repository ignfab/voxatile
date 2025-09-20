package com.ignfab.minalac.generator.parameters.models.values;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.AbsentValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Parameters for an {@link AbsentValue}.
 */
public class AbsentValueParams extends ModelValueParams {
    @Override
    public ModelValue create(Generation generation) {
         return AbsentValue.INSTANCE;
    }
}
