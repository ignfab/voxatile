package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnbuildableException.class, () -> new ConstantAxisMapperBuilder(7). build(8));
        assertThrows(UnbuildableException.class, () -> new ConstantAxisMapperBuilder(6). build(8));

        // TODO: not finished
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(-1, new ConstantAxisMapperBuilder(3).maxSizeUnder(-1));

        assertEquals(3, new ConstantAxisMapperBuilder(3).maxSizeUnder(4));
        assertEquals(3, new ConstantAxisMapperBuilder(3).maxSizeUnder(3));
        assertEquals(-1, new ConstantAxisMapperBuilder(3).maxSizeUnder(2));

        assertEquals(0, new ConstantAxisMapperBuilder(0).maxSizeUnder(0));
        assertEquals(0, new ConstantAxisMapperBuilder(0).maxSizeUnder(1));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(7, new ConstantAxisMapperBuilder(7).minimumSize());
        assertEquals(0, new ConstantAxisMapperBuilder(0).minimumSize());
    }
}
