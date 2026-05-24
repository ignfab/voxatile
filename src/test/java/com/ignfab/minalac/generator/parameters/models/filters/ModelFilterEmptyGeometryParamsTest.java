package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.filters.ModelFilterEmptyGeometry;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterEmptyGeometryParamsTest {
    @Test
    public void testCreate() {
        assertSame(ModelFilterEmptyGeometry.INSTANCE, new ModelFilterEmptyGeometryParams().create(TestingGeneration.UNUSED));
    }
}
