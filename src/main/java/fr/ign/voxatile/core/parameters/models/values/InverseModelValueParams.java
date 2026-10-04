package fr.ign.voxatile.core.parameters.models.values;

import java.beans.ConstructorProperties;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.InverseModelValue;
import fr.ign.voxatile.core.models.values.ModelValue;

/**
 * Parameters for {@link InverseModelValue}.
 */
public class InverseModelValueParams extends ModelValueParams {
    /**
     * Model value to compute inverse from (required).
     */
    public ModelValueParams inverse;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param inverse the model value to use
     */
    @ConstructorProperties("inverse")
    public InverseModelValueParams(ModelValueParams inverse) {
        this.inverse = inverse;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        inverse.validate();
    }

    @Override
    public ModelValue create(Generation generation) {
        return new InverseModelValue(inverse.create(generation));
    }
}
