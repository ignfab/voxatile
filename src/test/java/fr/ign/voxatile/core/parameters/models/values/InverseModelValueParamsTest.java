package fr.ign.voxatile.core.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.values.InverseModelValue;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class InverseModelValueParamsTest {
    @Test
    public void testDeserialize() {
        InverseModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(InverseModelValueParams.class, "inverse: 2"));
        assertInstanceOf(FixedValueParams.class, params.inverse);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(InverseModelValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(InverseModelValueParams.class, "{}"));
    }

    @Test
    public void testValidate() {
        InverseModelValueParams paramsValid = new InverseModelValueParams(TestingModelValueParams.VALID);
        assertDoesNotThrow(paramsValid::validate);

        InverseModelValueParams paramsInvalid = new InverseModelValueParams(TestingModelValueParams.INVALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid::validate);
    }
}
