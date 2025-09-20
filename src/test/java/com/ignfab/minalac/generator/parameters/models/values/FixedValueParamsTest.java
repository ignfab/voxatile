package com.ignfab.minalac.generator.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.FixedValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

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
