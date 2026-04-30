package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

public class AdjustAxisMapperBuilderTest {
    @Test
    public void foo() throws UnbuildableException {
        AxisMapperBuilder one = new TestingAxisMapperBuilder(2, 5);
        AxisMapperBuilder two = new TestingAxisMapperBuilder(3, 7);
        AxisMapperBuilder three = new TestingAxisMapperBuilder(4, 6);

        AxisMapperBuilder keep = new AdjustAxisMapperBuilder(one, two, three);
        System.out.println(keep.minimumSize());
        System.out.println(keep.maxSizeUnder(4));
        System.out.println(keep.maxSizeUnder(6));
        System.out.println(keep.maxSizeUnder(12));
        System.out.println(keep.maxSizeUnder(30));
    }
}
