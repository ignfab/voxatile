package com.ignfab.minalac.generator.parameters.processors.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class PostProcessorParamsDeserializerTest {
    @Test
    @DisplayName("Test multiple post-processing steps deserialization using list")
    public void testList() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(IdentityPostProcessorParams.class, "identity"));

        PostProcessorParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PostProcessorParams.class, """
            - type: identity
            - type: identity
            """, mapper));
        assertInstanceOf(SequentialPostProcessorParams.class, params);
    }
}
