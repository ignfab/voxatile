package com.ignfab.minalac.generator.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.InverseModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class InverseModelValueParamsTest {
    @Test
    public void testDeserialize() {
        InverseModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(InverseModelValueParams.class, "inverse: 2"));
        assertInstanceOf(FixedValueParams.class, params.inverse);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(InverseModelValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(InverseModelValueParams.class, "inverse:"));
    }

    @Test
    public void testValidate() {
        InverseModelValueParams paramsValid = new InverseModelValueParams(TestingModelValueParams.VALID);
        assertDoesNotThrow(paramsValid::validate);

        InverseModelValueParams paramsInvalid = new InverseModelValueParams(TestingModelValueParams.INVALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid::validate);
    }
}
