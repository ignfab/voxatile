package com.ignfab.minalac.generator.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.models.values.RandomUniformModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class RandomUniformModelValueParamsTest {
    @Test
    public void testDeserialize() {
        RandomUniformModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(RandomUniformModelValueParams.class, """
            min: 1
            max: 2
            seed: Corneille
            """));
        assertInstanceOf(FixedValueParams.class, params.min);
        assertInstanceOf(FixedValueParams.class, params.max);
        assertEquals("Corneille", params.seed);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(RandomUniformModelValue.class, value);

        RandomUniformModelValueParams params2 = assertDoesNotThrow(() -> ParamsTester.deserialize(RandomUniformModelValueParams.class, """
            min: 1
            max: 2
            """));
        assertEquals("", params2.seed);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(RandomUniformModelValueParams.class, """
            min:
            max: 2
            """));

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(RandomUniformModelValueParams.class, """
            min: 1
            max:
            """));
    }

    @Test
    public void testValidate() {
        RandomUniformModelValueParams paramsValid = new RandomUniformModelValueParams(TestingModelValueParams.VALID, TestingModelValueParams.VALID);
        assertDoesNotThrow(paramsValid::validate);

        RandomUniformModelValueParams paramsInvalid1 = new RandomUniformModelValueParams(TestingModelValueParams.INVALID, TestingModelValueParams.VALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid1::validate);

        RandomUniformModelValueParams paramsInvalid2 = new RandomUniformModelValueParams(TestingModelValueParams.VALID, TestingModelValueParams.INVALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid2::validate);
    }
}
