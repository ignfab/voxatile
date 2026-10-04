package fr.ign.voxatile.core.parameters.models.values;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.AbsentValue;
import fr.ign.voxatile.core.models.values.ModelValue;

/**
 * Parameters for an {@link AbsentValue}.
 */
public class AbsentValueParams extends ModelValueParams {
    @Override
    public ModelValue create(Generation generation) {
         return AbsentValue.INSTANCE;
    }
}
