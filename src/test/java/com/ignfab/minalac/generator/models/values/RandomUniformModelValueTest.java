package com.ignfab.minalac.generator.models.values;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;

public class RandomUniformModelValueTest {
    @Test
    public void test() {
        TestingSeed seed = new TestingSeed("s");
        seed.random().setNextDouble(0.5);
        Model model = new TestingModel("M");

        assertModelValue(new RandomUniformModelValue(new FixedValue(1), new FixedValue(2), seed), 1.5, model);

        assertModelValueAbsent(new RandomUniformModelValue(new FixedValue(1), AbsentValue.INSTANCE, seed), model);
        assertModelValueAbsent(new RandomUniformModelValue(AbsentValue.INSTANCE, new FixedValue(2), seed), model);
        assertModelValueAbsent(new RandomUniformModelValue(AbsentValue.INSTANCE, AbsentValue.INSTANCE, seed), model);

        assertModelValueAbsent(new RandomUniformModelValue(new FixedValue(2), new FixedValue(1), seed), model);
        assertModelValueAbsent(new RandomUniformModelValue(new FixedValue(1), new FixedValue(1), seed), model);
    }
}
