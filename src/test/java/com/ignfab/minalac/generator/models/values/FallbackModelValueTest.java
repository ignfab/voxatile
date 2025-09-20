package com.ignfab.minalac.generator.models.values;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;

public class FallbackModelValueTest {
    @Test
    public void test() {
        assertModelValue(new FallbackModelValue(new FixedValue(1), new FixedValue(2)), 1);
        assertModelValue(new FallbackModelValue(new FixedValue(1), AbsentValue.INSTANCE), 1);
        assertModelValue(new FallbackModelValue(AbsentValue.INSTANCE, new FixedValue(2)), 2);
        assertModelValueAbsent(new FallbackModelValue(AbsentValue.INSTANCE, AbsentValue.INSTANCE));
    }
}
