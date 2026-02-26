package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.util.List;
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

public class TileSequenceTaskParamsTest {

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
        TileSequenceTaskParams params;

        // Model selection validation
        params = new TileSequenceTaskParams(List.of(TestingTaskParams.VALID));
        assertDoesNotThrow(params::validate);

        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);

        // Check validation propagation
        params = new TileSequenceTaskParams(List.of(new InvalidTaskParams()));
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testModelSelection() {
        // Just ensure model selection merging has not been forgotten
        TestingTaskParams task1 = new TestingTaskParams();
        TestingTaskParams task2 = new TestingTaskParams();
        task1.models.type = "foo";

        TileSequenceTaskParams params = new TileSequenceTaskParams(List.of(task1, task2));
        params.models.type = "bar";

        Map<String, TaskParams<GenerationTile>> additional = params.flatten("test");
        TestingTaskParams test1 = assertInstanceOf(TestingTaskParams.class, additional.get("test:1"));
        assertEquals(ModelSelection.NONE, test1.models.create());
        TestingTaskParams test2 = assertInstanceOf(TestingTaskParams.class, additional.get("test:2"));
        assertEquals("bar", test2.models.type);
    }
}
