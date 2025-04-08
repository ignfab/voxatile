package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.processors.post.IdentityPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityPostProcessorParamsTest {
    @Test
    public void testCreate() {
        assertSame(IdentityPostProcessor.INSTANCE, new IdentityPostProcessorParams().create());
    }
}
