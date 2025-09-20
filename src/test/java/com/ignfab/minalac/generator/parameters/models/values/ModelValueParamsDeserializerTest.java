package com.ignfab.minalac.generator.parameters.models.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class ModelValueParamsDeserializerTest {
    @Test
    @DisplayName("Test model value deserialization using number shortcut")
    public void testModelValueParamsDeserializerNumber() {
        // Decimal
        ModelValueParams params1 = assertDoesNotThrow(() -> ParamsTester.deserialize(ModelValueParams.class, "7.5"));
        FixedValueParams fvp1 = assertInstanceOf(FixedValueParams.class, params1);
        assertEquals(7.5, fvp1.fixed);

        // Integer
        ModelValueParams params2 = assertDoesNotThrow(() -> ParamsTester.deserialize(ModelValueParams.class, "-3"));
        FixedValueParams fvp2 = assertInstanceOf(FixedValueParams.class, params2);
        assertEquals(-3, fvp2.fixed);
    }

    @Test
    @DisplayName("Test model value deserialization using string shortcut")
    public void testModelValueParamsDeserializerString() {
        ModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ModelValueParams.class, "height"));
        MetadataValueParams mvp = assertInstanceOf(MetadataValueParams.class, params);
        assertEquals("height", mvp.metadata);
    }

    @Test
    @DisplayName("Test model value deserialization using 'absent' shortcut")
    public void testModelValueParamsDeserializerAbsent() {
        ModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ModelValueParams.class, "absent"));
        assertInstanceOf(AbsentValueParams.class, params);
    }

    @Test
    @DisplayName("Test model value deserialization using Jackson deduction")
    public void testModelValueParamsDeserializerDeduction() {
        ModelValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ModelValueParams.class, "fixed: 1"));
        FixedValueParams fvp = assertInstanceOf(FixedValueParams.class, params);
        assertEquals(1, fvp.fixed);
    }
}
