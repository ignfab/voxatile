package fr.ign.voxatile.core.parameters.models.filters;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.filters.ModelFilterOnValue;
import fr.ign.voxatile.core.parameters.models.values.FixedValueParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterValueEqualsParamsTest {
    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterValueEqualsParams(TestingModelValueParams.VALID, 1);
        assertDoesNotThrow(valid::validate);

        ModelFilterParams invalid = new ModelFilterValueEqualsParams(TestingModelValueParams.INVALID, 1);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterValueEqualsParams(new FixedValueParams(1), 1);
        assertInstanceOf(ModelFilterOnValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}
