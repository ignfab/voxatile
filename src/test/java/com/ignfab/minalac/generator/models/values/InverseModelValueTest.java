package com.ignfab.minalac.generator.models.values;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;
import static com.ignfab.minalac.generator.models.values.ModelValueTester.assertModelValueAbsent;

public class InverseModelValueTest {
    @Test
    public void test() {
        assertModelValue(new InverseModelValue(new FixedValue(2)), 0.5);
        assertModelValueAbsent(new InverseModelValue(AbsentValue.INSTANCE));
        assertModelValueAbsent(new InverseModelValue(new FixedValue(0)));
    }
}
