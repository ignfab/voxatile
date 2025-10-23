package com.ignfab.minalac.generator.parameters.tasks;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceTaskParamsTest {

    @Test
    public void testValidate() {
        SequenceTaskParams params;

        // Model selection validation
        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID));
        assertDoesNotThrow(params::validate);

        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);

        // Subtasks validation
        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID, TestingTaskParams.VALID));
        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID, TestingTaskParams.INVALID));
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        // `after` validation (done by TileTaskParams but we test that validation is triggered)
        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID));
        params.after.add("a:a");
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        // `using` validation (done by ControlTaskParams but we test that validation is triggered)
        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID));
        params.using.add("a:a");
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreateAditionalTaskParams() {
        SequenceTaskParams params;
        Map<String, TileTaskParams> additional;

        // Base sequence task
        params = new SequenceTaskParams(List.of(
                new TestingTaskParams(),
                new TestingTaskParams(),
                new TestingTaskParams()
            ));

        additional = params.createAditionalTaskParams("test");
        ScheduleParamTester.assertValidSchedule(additional);
        assertTrue(additional.containsKey("test:1"));
        assertTrue(additional.containsKey("test:2"));
        assertTrue(additional.containsKey("test:3"));
        ScheduleParamTester.assertPredecessor(additional, "test:1", "test:2");
        ScheduleParamTester.assertPredecessor(additional, "test:2", "test:3");

        // Sequence in sequence
        params = new SequenceTaskParams(List.of(
            new TestingTaskParams(),
            new SequenceTaskParams(List.of(
                new TestingTaskParams(),
                new TestingTaskParams(),
                new TestingTaskParams()
            )),
            new TestingTaskParams()
        ));

        additional = params.createAditionalTaskParams("test");

        ScheduleParamTester.assertValidSchedule(additional);
        assertTrue(additional.containsKey("test:1"));
        assertTrue(additional.containsKey("test:2"));
        assertTrue(additional.containsKey("test:2:1"));
        assertTrue(additional.containsKey("test:2:2"));
        assertTrue(additional.containsKey("test:2:3"));
        assertTrue(additional.containsKey("test:3"));

        ScheduleParamTester.assertPredecessor(additional, "test:1", "test:2:1");
        ScheduleParamTester.assertPredecessor(additional, "test:2:1", "test:2:2");
        ScheduleParamTester.assertPredecessor(additional, "test:2:2", "test:2:3");
        ScheduleParamTester.assertPredecessor(additional, "test:2:3", "test:3");
    }

    @Test
    public void testCreateAditionalTaskParamsAfter() {
        SequenceTaskParams sequence = new SequenceTaskParams(List.of(
            new TestingTaskParams(),
            new TestingTaskParams()
        ));

        TestingTaskParams last = new TestingTaskParams();

         ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
            "first", new TestingTaskParams(),
            "last", last,
            "other", new TestingTaskParams(),
            "sequence", sequence
        ));

        sequence.after.add("first");
        last.after.add("sequence");

        Map<String, TileTaskParams> additional = params.createAditionalTaskParams("test");

        // Checks both task after sequence and sequence after task

        ScheduleParamTester.assertValidSchedule(additional);
        ScheduleParamTester.assertPredecessor(additional, "test:sequence:2", "test:last");
        ScheduleParamTester.assertPredecessor(additional, "test:first", "test:sequence:1");
        ScheduleParamTester.assertNotPredecessor(additional, "test:other", "test:sequence:1");
        ScheduleParamTester.assertNotPredecessor(additional, "test:sequence:2", "test:other");
    }

    @Test
    public void testCreateAditionalTaskParamsUsing() {

        TestingTaskParams subtask = new TestingTaskParams();
        TestingTaskParams subsubtask = new TestingTaskParams();

        // Beware: sequence order may induce unwanted dependencies

        SequenceTaskParams subsequence = new SequenceTaskParams(List.of(
            new TestingTaskParams(), // sequence:2:1
            subsubtask               // sequence:2:2
        ));

        SequenceTaskParams sequence = new SequenceTaskParams(List.of(
            new TestingTaskParams(), // sequence:1
            subsequence,             // sequence:2
            subtask                  // sequence:3
        ));

        ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
            "sequence", sequence,
            "task", new TestingTaskParams(),
            "othertask", new TestingTaskParams()
        ));

        subtask.after.add("task");
        sequence.using.add("task");
        subsubtask.after.add("task");
        subsequence.using.add("task");

        Map<String, TileTaskParams> additional = assertDoesNotThrow(() -> params.createAditionalTaskParams("main"));

        ScheduleParamTester.assertValidSchedule(additional);
        ScheduleParamTester.assertPredecessor(additional, "main:task", "main:sequence:3");
        ScheduleParamTester.assertPredecessor(additional, "main:task", "main:sequence:2:2");
        ScheduleParamTester.assertNotPredecessor(additional, "main:task", "main:sequence:2:1");
        ScheduleParamTester.assertNotPredecessor(additional, "main:task", "main:sequence:1");
        ScheduleParamTester.assertNotPredecessor(additional, "main:sequence:3", "main:sequence:2:2");
        ScheduleParamTester.assertNotPredecessor(additional, "main:othertask", "main:sequence:3");
    }

}
