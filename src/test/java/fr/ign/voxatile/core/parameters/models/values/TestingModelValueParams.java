package fr.ign.voxatile.core.parameters.models.values;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.AbsentValue;
import fr.ign.voxatile.core.models.values.ModelValue;

/**
 * A {@code ModelValueParams} for testing purposes.
 */
public class TestingModelValueParams extends ModelValueParams {
    private final boolean valid;

    /**
     * An invalid testing model value params.
     */
    public static final TestingModelValueParams INVALID = new TestingModelValueParams(false);
    /**
     * A valid testing model value params.
     */
    public static final TestingModelValueParams VALID = new TestingModelValueParams(true);

    /**
     * Creates a new {@code TestingModelValueParams}.
     * @param valid testing field
     */
    public TestingModelValueParams(boolean valid) {
        this.valid = valid;
    }

    @Override
    public void validate() {
        if (!valid)
            throw new IllegalArgumentException();
    }

    @Override
    public ModelValue create(Generation generation) {
        return AbsentValue.INSTANCE;
    }
}
