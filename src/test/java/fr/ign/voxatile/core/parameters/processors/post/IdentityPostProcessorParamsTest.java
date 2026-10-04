package fr.ign.voxatile.core.parameters.processors.post;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.processors.post.IdentityPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityPostProcessorParamsTest {
    @Test
    public void testCreate() {
        assertSame(IdentityPostProcessor.INSTANCE, new IdentityPostProcessorParams().create(TestingGeneration.UNUSED));
    }
}
