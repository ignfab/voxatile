package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

public class KeepAxisMapperBuilderTest {
    @Test
    public void foo() {
        AxisMapperBuilder one = new TestingAxisMapperBuilder(2, 5);
        AxisMapperBuilder two = new TestingAxisMapperBuilder(3, 7);
        AxisMapperBuilder three = new TestingAxisMapperBuilder(6, 6);

        AxisMapperBuilder keep = new KeepAxisMapperBuilder(one, two, three);
        System.out.println(keep.minimumSize());
        System.out.println(keep.maxSizeUnder(4));
        System.out.println(keep.maxSizeUnder(6));
        System.out.println(keep.maxSizeUnder(12));
        System.out.println(keep.maxSizeUnder(30));


        if (true)
            throw new RuntimeException("not implmeted");
    }
}
