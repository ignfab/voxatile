package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.TestingTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Task;

import static org.junit.jupiter.api.Assertions.*;

public class TileScheduleTaskParamsTest {

    private class InvalidTaskParams extends TileTaskParams {
        @Override
        public void validate() {
            throw new IllegalArgumentException();
        }

        @Override
        public Task<GenerationTile> create(Generation generation) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testValidate() {
        TileScheduleTaskParams params;

        // Model selection validation
        params = new TileScheduleTaskParams(Map.of("a", TestingTaskParams.VALID));
        assertDoesNotThrow(params::validate);

        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);

        // Check validation propagation
        params = new TileScheduleTaskParams(Map.of("a", new InvalidTaskParams()));
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testModelSelection() {
        // Just ensure model selection merging has not been forgotten
        TestingTaskParams foo = new TestingTaskParams();
        TestingTaskParams bar = new TestingTaskParams();
        foo.models.type = "foo";

        TileScheduleTaskParams params = new TileScheduleTaskParams(Map.of(
            "foo", foo,
            "bar", bar
        ));
        params.models.type = "bar";

        Map<String, TaskParams<GenerationTile>> additional = params.flatten("test");
        TestingTaskParams testFoo = assertInstanceOf(TestingTaskParams.class, additional.get("test:foo"));
        assertEquals(ModelSelection.NONE, testFoo.models.create());
        TestingTaskParams testBar = assertInstanceOf(TestingTaskParams.class, additional.get("test:bar"));
        assertEquals("bar", testBar.models.type);
    }

}
