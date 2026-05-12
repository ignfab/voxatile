package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnbuildableException.class, () -> new ConstantAxisMapperBuilder(7, 0).build(8));
        assertThrows(UnbuildableException.class, () -> new ConstantAxisMapperBuilder(6, 0).build(8));

        // TODO-PR-Facade: not finished
        throw new RuntimeException("TODO: not finished");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(-1, new ConstantAxisMapperBuilder(3, 0).maxSizeUnder(-1));

        assertEquals(3, new ConstantAxisMapperBuilder(3, 0).maxSizeUnder(4));
        assertEquals(3, new ConstantAxisMapperBuilder(3, 0).maxSizeUnder(3));
        assertEquals(-1, new ConstantAxisMapperBuilder(3, 0).maxSizeUnder(2));

        assertEquals(0, new ConstantAxisMapperBuilder(0, 0).maxSizeUnder(0));
        assertEquals(0, new ConstantAxisMapperBuilder(0, 0).maxSizeUnder(1));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(7, new ConstantAxisMapperBuilder(7, 0).minimumSize());
        assertEquals(0, new ConstantAxisMapperBuilder(0, 0).minimumSize());
    }
}
