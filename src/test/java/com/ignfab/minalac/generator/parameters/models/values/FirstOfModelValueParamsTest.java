package com.ignfab.minalac.generator.parameters.models.values;

import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.FallbackModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class FirstOfModelValueParamsTest {
    @Test
    public void testDeserialize() {
        FirstOfModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FirstOfModelValueParams.class, """
            firstOf:
              - 1
              - 2
            """));
        assertEquals(2, params.firstOf.size());
        assertInstanceOf(FixedValueParams.class, params.firstOf.get(0));
        assertInstanceOf(FixedValueParams.class, params.firstOf.get(1));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(FallbackModelValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(FirstOfModelValueParams.class, "firstOf:"));
    }

    @Test
    public void testValidate() {
        FirstOfModelValueParams paramsValid = new FirstOfModelValueParams(List.of(TestingModelValueParams.VALID));
        assertDoesNotThrow(paramsValid::validate);

        FirstOfModelValueParams paramsInvalid1 = new FirstOfModelValueParams(List.of());
        assertThrows(IllegalArgumentException.class, paramsInvalid1::validate);

        FirstOfModelValueParams paramsInvalid2 = new FirstOfModelValueParams(List.of(TestingModelValueParams.VALID, TestingModelValueParams.INVALID));
        assertThrows(IllegalArgumentException.class, paramsInvalid2::validate);
    }
}
