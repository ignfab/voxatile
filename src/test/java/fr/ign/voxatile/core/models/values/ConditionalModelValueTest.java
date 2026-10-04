package fr.ign.voxatile.core.models.values;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.TestingModel;
import fr.ign.voxatile.core.models.filters.TestingModelFilter;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;

public class ConditionalModelValueTest {
    @Test
    public void test() {
        Model modelA = new TestingModel("A");
        Model modelB = new TestingModel("B");

        ConditionalModelValue value1 = new ConditionalModelValue(new TestingModelFilter(modelA), new FixedValue(1), new FixedValue(2));
        assertModelValue(value1, 1, modelA);
        assertModelValue(value1, 2, modelB);

        ConditionalModelValue value2 = new ConditionalModelValue(new TestingModelFilter(modelA), new FixedValue(-3), AbsentValue.INSTANCE);
        assertModelValue(value2, -3, modelA);
        assertModelValueAbsent(value2, modelB);
    }
}
