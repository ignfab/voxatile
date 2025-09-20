package com.ignfab.minalac.generator.parameters.models.values;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.values.AbsentValue;

import static org.junit.jupiter.api.Assertions.*;

public class AbsentValueParamsTest {
    @Test
    public void testCreate() {
        assertSame(AbsentValue.INSTANCE, new AbsentValueParams().create(TestingGeneration.UNUSED));
    }
}
