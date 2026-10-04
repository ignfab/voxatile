package fr.ign.voxatile.core.parameters.models.values;

import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.TestingModel;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.models.values.UnaryOperationModelValue;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class SingleOperandModelValueParamsTest {
    @Test
    public void testDeserializeRound() {
        SingleOperandModelValueParams.Round params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            SingleOperandModelValueParams.Round.class,
            "round: { metadata: height }"
        ));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(UnaryOperationModelValue.class, value);

        assertModelValue(value, 0, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 2, new TestingModel(Map.of("height", 2.2)));
        assertModelValue(value, 3, new TestingModel(Map.of("height", 2.7)));
    }

    @Test
    public void testDeserializeFloor() {
        SingleOperandModelValueParams.Floor params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            SingleOperandModelValueParams.Floor.class,
            "floor: { metadata: height }"
        ));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(UnaryOperationModelValue.class, value);

        assertModelValue(value, 0, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 2, new TestingModel(Map.of("height", 2.2)));
        assertModelValue(value, 2, new TestingModel(Map.of("height", 2.7)));
    }

    @Test
    public void testDeserializeCeil() {
        SingleOperandModelValueParams.Ceil params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            SingleOperandModelValueParams.Ceil.class,
            "ceil: { metadata: height }"
        ));

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(UnaryOperationModelValue.class, value);

        assertModelValue(value, 0, new TestingModel(Map.of("height", 0)));
        assertModelValue(value, 3, new TestingModel(Map.of("height", 2.2)));
        assertModelValue(value, 3, new TestingModel(Map.of("height", 2.7)));
    }

    @Test
    public void testValidate() {
        // All subclasses use the same validate()
        assertDoesNotThrow(new SingleOperandModelValueParams.Round(TestingModelValueParams.VALID)::validate);

        assertThrows(IllegalArgumentException.class, new SingleOperandModelValueParams.Round(TestingModelValueParams.INVALID)::validate);
    }
}
