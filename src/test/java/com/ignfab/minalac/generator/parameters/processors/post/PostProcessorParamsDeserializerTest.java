package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class PostProcessorParamsDeserializerTest {
    @Test
    @DisplayName("Test multiple post-processing steps deserialization using list")
    public void testList() {
        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("identity", IdentityPostProcessorParams.class);

        PostProcessorParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PostProcessorParams.class, """
            - type: identity
            - type: identity
            """, builder));
        assertInstanceOf(SequentialPostProcessorParams.class, params);
    }
}
