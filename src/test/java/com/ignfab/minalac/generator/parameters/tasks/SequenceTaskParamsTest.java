package com.ignfab.minalac.generator.parameters.tasks;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.ModelSelection;
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

        // `using` validation
        params = new SequenceTaskParams(List.of(TestingTaskParams.VALID));
        params.using.add("a:a");
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testFlatten() {
        SequenceTaskParams params;
        Map<String, TaskParams> schedule;

        // Base sequence task
        params = new SequenceTaskParams(List.of(
            new TestingTaskParams(),
            new TestingTaskParams(),
            new TestingTaskParams()
        ));

        schedule = params.flatten("test");
        ScheduleParamsTester.assertValidSchedule(schedule);
        assertTrue(schedule.containsKey("test:1"));
        assertTrue(schedule.containsKey("test:2"));
        assertTrue(schedule.containsKey("test:3"));
        ScheduleParamsTester.assertPredecessor(schedule, "test:1", "test:2");
        ScheduleParamsTester.assertPredecessor(schedule, "test:2", "test:3");

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

        schedule = params.flatten("test");

        ScheduleParamsTester.assertValidSchedule(schedule);
        assertTrue(schedule.containsKey("test:1"));
        assertTrue(schedule.containsKey("test:2"));
        assertTrue(schedule.containsKey("test:2:1"));
        assertTrue(schedule.containsKey("test:2:2"));
        assertTrue(schedule.containsKey("test:2:3"));
        assertTrue(schedule.containsKey("test:3"));

        ScheduleParamsTester.assertPredecessor(schedule, "test:1", "test:2:1");
        ScheduleParamsTester.assertPredecessor(schedule, "test:2:1", "test:2:2");
        ScheduleParamsTester.assertPredecessor(schedule, "test:2:2", "test:2:3");
        ScheduleParamsTester.assertPredecessor(schedule, "test:2:3", "test:3");
    }

    @Test
    public void testFlattenAfter() {
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

        Map<String, TaskParams> schedule = params.flatten("test");

        // Checks both task after sequence and sequence after task

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertPredecessor(schedule, "test:sequence:2", "test:last");
        ScheduleParamsTester.assertPredecessor(schedule, "test:first", "test:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(schedule, "test:other", "test:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(schedule, "test:sequence:2", "test:other");
    }

    @Test
    public void testFlattenUsing() {

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

        Map<String, TaskParams> schedule = assertDoesNotThrow(() -> params.flatten("main"));

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertPredecessor(schedule, "main:task", "main:sequence:3", "main:sequence:2:2");
        ScheduleParamsTester.assertNotPredecessor(schedule, "main:task", "main:sequence:2:1", "main:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(schedule, "main:sequence:3", "main:sequence:2:2", "main:sequence:3");
    }

    @Test
    public void testFlattenModelSelection() {
        // Just ensure model selection merging has not been forgotten
        TestingTaskParams task1 = new TestingTaskParams();
        TestingTaskParams task2 = new TestingTaskParams();
        task1.models.type = "foo";

        SequenceTaskParams params = new SequenceTaskParams(List.of(task1, task2));
        params.models.type = "bar";

        Map<String, TaskParams> schedule = params.flatten("test");
        TestingTaskParams test1 = assertInstanceOf(TestingTaskParams.class, schedule.get("test:1"));
        assertEquals(ModelSelection.NONE, test1.models.create());
        TestingTaskParams test2 = assertInstanceOf(TestingTaskParams.class, schedule.get("test:2"));
        assertEquals("bar", test2.models.type);
    }
}
