package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.models.values.AbsentValueParams;
import com.ignfab.minalac.generator.parameters.models.values.FixedValueParams;
import com.ignfab.minalac.generator.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataSetPostProcessorParamsTest {
    @Test
    public void testCreate() {
        MetadataSetPostProcessorParams params = new MetadataSetPostProcessorParams("toto", new FixedValueParams(1));
        assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new MetadataSetPostProcessorParams("toto", TestingModelValueParams.VALID)::validate);
        assertThrows(IllegalArgumentException.class, new MetadataSetPostProcessorParams("toto", TestingModelValueParams.INVALID)::validate);
        assertThrows(IllegalArgumentException.class, new MetadataSetPostProcessorParams("", TestingModelValueParams.VALID)::validate);
    }

    @Test
    public void testDeserialization() {
        YAMLMapper.Builder builder = ParamsTester.mapperBuilderWithParams("set", MetadataSetPostProcessorParams.class);

        MetadataSetPostProcessorParams requiredParams = assertDoesNotThrow(() -> ParamsTester.deserialize(MetadataSetPostProcessorParams.class, """
            type: set
            metadata: height
            value: 5
            """, builder));
        assertEquals("height", requiredParams.metadata);
        FixedValueParams fvp = assertInstanceOf(FixedValueParams.class, requiredParams.value);
        assertEquals(5, fvp.fixed);
        assertFalse(requiredParams.abortIfValueIsAbsent);
        assertFalse(requiredParams.keepExisting);

        MetadataSetPostProcessorParams requiredAndOptionalParams = assertDoesNotThrow(() -> ParamsTester.deserialize(MetadataSetPostProcessorParams.class, """
            type: set
            metadata: toto
            value: absent
            keepExisting: true
            abortIfValueIsAbsent: true
            """, builder));
        assertEquals("toto", requiredAndOptionalParams.metadata);
        assertInstanceOf(AbsentValueParams.class, requiredAndOptionalParams.value);
        assertTrue(requiredAndOptionalParams.abortIfValueIsAbsent);
        assertTrue(requiredAndOptionalParams.keepExisting);
    }
}
