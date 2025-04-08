package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.processors.post.DiscardPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPostProcessorParamsTest {
    @Test
    public void testCreate() {
        assertSame(DiscardPostProcessor.INSTANCE, new DiscardPostProcessorParams().create());
    }
}
