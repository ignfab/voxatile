package com.ignfab.minalac.generator.models.values;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;

public class AbsentValueTest {
    @Test
    public void test() {
        assertModelValueAbsent(AbsentValue.INSTANCE);
    }
}
