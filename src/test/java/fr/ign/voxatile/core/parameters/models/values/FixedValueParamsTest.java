package fr.ign.voxatile.core.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.values.FixedValue;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class FixedValueParamsTest {
    @Test
    public void testDeserialize() {
        FixedValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FixedValueParams.class, "fixed: 4"));
        assertEquals(4, params.fixed);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(FixedValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(FixedValueParams.class, "fixed:"));
    }
}
