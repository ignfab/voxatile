package fr.ign.voxatile.core.parameters.models.values;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.values.AbsentValue;

import static org.junit.jupiter.api.Assertions.*;

public class AbsentValueParamsTest {
    @Test
    public void testCreate() {
        assertSame(AbsentValue.INSTANCE, new AbsentValueParams().create(TestingGeneration.UNUSED));
    }
}
