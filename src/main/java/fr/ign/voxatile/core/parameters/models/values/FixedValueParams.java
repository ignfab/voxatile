package fr.ign.voxatile.core.parameters.models.values;

import java.beans.ConstructorProperties;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.FixedValue;
import fr.ign.voxatile.core.models.values.ModelValue;

/**
 * Parameters for a {@link FixedValue}.
 */
public class FixedValueParams extends ModelValueParams {
    /**
     * The fixed value (required).
     */
    public double fixed;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param fixed the fixed model value
     */
    @ConstructorProperties("fixed")
    public FixedValueParams(double fixed) {
        this.fixed = fixed;
    }

    @Override
    public ModelValue create(Generation generation) {
        return new FixedValue(fixed);
    }
}
