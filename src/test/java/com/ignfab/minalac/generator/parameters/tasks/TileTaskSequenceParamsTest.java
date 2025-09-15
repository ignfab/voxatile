package com.ignfab.minalac.generator.parameters.tasks;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.tasks.TileTask;

import static org.junit.jupiter.api.Assertions.*;


public class TileTaskSequenceParamsTest {
    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(TileTaskSequenceParams.class, "sequence"));
        mapper.registerSubtypes(new NamedType(TileTaskSequenceParamsTest.TestingParams.class, "test"));

        // Minimal example
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            TileTaskSequenceParams.class,
            """
                type: sequence
                tasks:
                  - type: test
            """,
            mapper));

        // Fatter example
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            TileTaskSequenceParams.class,
            """
                type: sequence
                after: foo
                models:
                  type: bar
                tasks:
                  - type: test
                  - type: test
                  - type: test
            """,
            mapper));
    }

    @Test
    public void testValidate() {
        TileTaskSequenceParams params;

        // Test cases about tasks
        params = new TileTaskSequenceParams(List.of());
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new TileTaskSequenceParams(List.of(TestingParams.VALID));
        assertDoesNotThrow(params::validate);

        TestingParams task = new TestingParams();
        task.after.add("baz");
        params = new TileTaskSequenceParams(List.of(task));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new TileTaskSequenceParams(List.of(
            TestingParams.VALID, TestingParams.INVALID, TestingParams.VALID
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // Test cases about model selection
        params = new TileTaskSequenceParams(List.of(TestingParams.VALID));
        params.models = new ModelSelectionParams();
        assertThrows(IllegalArgumentException.class, params::validate);

        params.models.type = "foo";
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testCreate() {
        // TODO
    }

    static final class TestingParams extends ModelTaskParams {
        /**
         * A valid TestingParams.
         */
        public static final TestingParams VALID = new TestingParams(true);
        /**
         * An invalid TestingParams.
         */
        public static final TestingParams INVALID = new TestingParams(false);

        private boolean valid = true;

        /**
         * Creates a new valid TestingParams.
         */
        TestingParams() {
            this(true);
        }

        private TestingParams(boolean valid) {
            this.valid = valid;
            if (valid)
                this.models = TestingModelSelectionParams.VALID;

        }

        @Override
        public void validate() {
            super.validate();

            if (!valid)
                throw new IllegalArgumentException();
        }

        @Override
        public TileTask create(Generation generation) {
            throw new UnsupportedOperationException("Unimplemented method 'create'");
        }

    }
}
