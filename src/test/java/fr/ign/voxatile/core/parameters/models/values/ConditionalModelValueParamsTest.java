package fr.ign.voxatile.core.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.values.ConditionalModelValue;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.parameters.models.filters.ModelFilterHasMetadataParams;
import fr.ign.voxatile.core.parameters.models.filters.TestingModelFilterParams;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionalModelValueParamsTest {
    @Test
    public void testDeserialize() {
        ConditionalModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ConditionalModelValueParams.class, """
            if:
              hasMetadata: type
            then: 1
            else: 2
            """));
        assertInstanceOf(ModelFilterHasMetadataParams.class, params.condition);
        assertInstanceOf(FixedValueParams.class, params.valueIfTrue);
        assertInstanceOf(FixedValueParams.class, params.valueIfFalse);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(ConditionalModelValue.class, value);

        ConditionalModelValueParams params2 = assertDoesNotThrow(() -> ParamsTester.deserialize(ConditionalModelValueParams.class, """
            if:
              hasMetadata: type
            then: 1
            """));
        assertInstanceOf(AbsentValueParams.class, params2.valueIfFalse);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(ConditionalModelValueParams.class, """
            if:
            then: 1
            """));

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(ConditionalModelValueParams.class, """
            if:
              hasMetadata: type
            """));
    }

    @Test
    public void testValidate() {
        ConditionalModelValueParams paramsValid = new ConditionalModelValueParams(TestingModelFilterParams.VALID, TestingModelValueParams.VALID);
        assertDoesNotThrow(paramsValid::validate);

        paramsValid.valueIfFalse = TestingModelValueParams.VALID;
        assertDoesNotThrow(paramsValid::validate);

        ConditionalModelValueParams paramsInvalid1 = new ConditionalModelValueParams(TestingModelFilterParams.INVALID, TestingModelValueParams.VALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid1::validate);

        ConditionalModelValueParams paramsInvalid2 = new ConditionalModelValueParams(TestingModelFilterParams.VALID, TestingModelValueParams.INVALID);
        assertThrows(IllegalArgumentException.class, paramsInvalid2::validate);

        ConditionalModelValueParams paramsInvalid3 = new ConditionalModelValueParams(TestingModelFilterParams.VALID, TestingModelValueParams.VALID);
        paramsInvalid3.valueIfFalse = TestingModelValueParams.INVALID;
        assertThrows(IllegalArgumentException.class, paramsInvalid3::validate);
    }
}
