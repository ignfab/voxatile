package com.ignfab.minalac.generator.parameters.models.values;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.models.values.BinaryOperationModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class MultiOperandsModelValueParamsTest {
    @Test
    public void testDeserializeSum() {
        MultiOperandsModelValueParams.Sum params = assertDoesNotThrow(() -> ParamsTester.deserialize(MultiOperandsModelValueParams.Sum.class, """
            sum:
              - height
              - 3
              - 7
            """));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(BinaryOperationModelValue.class, value);

        assertModelValue(value, 10, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 12, new TestingModel(Map.of("height", 2)));
        assertModelValue(value, 13, new TestingModel(Map.of("height", 3)));
    }

    @Test
    public void testDeserializeProduct() {
        MultiOperandsModelValueParams.Product params = assertDoesNotThrow(() -> ParamsTester.deserialize(MultiOperandsModelValueParams.Product.class, """
            product:
              - height
              - 2
              - 5
            """));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(BinaryOperationModelValue.class, value);

        assertModelValue(value, 0, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 20, new TestingModel(Map.of("height", 2)));
        assertModelValue(value, 30, new TestingModel(Map.of("height", 3)));
    }

    @Test
    public void testDeserializeLowest() {
        MultiOperandsModelValueParams.Lowest params = assertDoesNotThrow(() -> ParamsTester.deserialize(MultiOperandsModelValueParams.Lowest.class, """
            lowest:
              - height
              - 2
              - 5
            """));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(BinaryOperationModelValue.class, value);

        assertModelValue(value, 0, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 2, new TestingModel(Map.of("height", 2)));
        assertModelValue(value, 2, new TestingModel(Map.of("height", 3)));
    }

    @Test
    public void testDeserializeHighest() {
        MultiOperandsModelValueParams.Highest params = assertDoesNotThrow(() -> ParamsTester.deserialize(MultiOperandsModelValueParams.Highest.class, """
            highest:
              - height
              - 2
              - 5
            """));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(BinaryOperationModelValue.class, value);

        assertModelValue(value, 5, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 5, new TestingModel(Map.of("height", 5)));
        assertModelValue(value, 7, new TestingModel(Map.of("height", 7)));
    }

    @Test
    public void testValidate() {
        // All subclasses use the same validate()
        assertThrows(IllegalArgumentException.class, new MultiOperandsModelValueParams.Sum(Collections.emptyList())::validate);

        // Testing first validation is propagated.
        MultiOperandsModelValueParams.Sum paramsFirstInvalid = new MultiOperandsModelValueParams.Sum(
            List.of(
                TestingModelValueParams.INVALID,
                TestingModelValueParams.VALID
            )
        );
        assertThrows(IllegalArgumentException.class, paramsFirstInvalid::validate);

        // Testing second validation is propagated.
        MultiOperandsModelValueParams.Product paramsSecondInvalid = new MultiOperandsModelValueParams.Product(
            List.of(
                TestingModelValueParams.VALID,
                TestingModelValueParams.INVALID
            )
        );
        assertThrows(IllegalArgumentException.class, paramsSecondInvalid::validate);
    }
}
