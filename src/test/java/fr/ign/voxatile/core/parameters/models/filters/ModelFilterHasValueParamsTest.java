package fr.ign.voxatile.core.parameters.models.filters;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.parameters.models.values.FixedValueParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterHasValueParamsTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterHasValueParams(new FixedValueParams(1)));
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new ModelFilterHasValueParams(TestingModelValueParams.VALID)::validate);
        assertThrows(IllegalArgumentException.class, new ModelFilterHasValueParams(TestingModelValueParams.INVALID)::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterHasValueParams params = new ModelFilterHasValueParams(new FixedValueParams(1));
        assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
    }
}
