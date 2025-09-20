package com.ignfab.minalac.generator.parameters.models.values;

import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.FallbackModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class FallbackModelValueParamsTest {
    @Test
    public void testDeserialize() {
        FallbackModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FallbackModelValueParams.class, """
            fallback:
              - 1
              - 2
            """));
        assertEquals(2, params.fallback.size());
        assertInstanceOf(FixedValueParams.class, params.fallback.get(0));
        assertInstanceOf(FixedValueParams.class, params.fallback.get(1));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(FallbackModelValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(FallbackModelValueParams.class, "fallback:"));
    }

    @Test
    public void testValidate() {
        FallbackModelValueParams paramsValid = new FallbackModelValueParams(List.of(TestingModelValueParams.VALID));
        assertDoesNotThrow(paramsValid::validate);

        FallbackModelValueParams paramsInvalid1 = new FallbackModelValueParams(List.of());
        assertThrows(IllegalArgumentException.class, paramsInvalid1::validate);

        FallbackModelValueParams paramsInvalid2 = new FallbackModelValueParams(List.of(TestingModelValueParams.VALID, TestingModelValueParams.INVALID));
        assertThrows(IllegalArgumentException.class, paramsInvalid2::validate);
    }
}
