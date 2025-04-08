package com.ignfab.minalac.generator.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityPostProcessorTest {
    @Test
    public void testProcess() {
        Model model = new TestingModel();
        assertSame(model, IdentityPostProcessor.INSTANCE.process(model));
    }
}
