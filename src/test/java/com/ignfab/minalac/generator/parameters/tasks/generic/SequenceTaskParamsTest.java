package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.tasks.ScheduleParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceTaskParamsTest {

    @Test
    public void testValidate() {
        SequenceTaskParams<Object> params;

        // Subtasks validation
        params = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(),
            new NoOperationTaskParams<>()
        ));
        assertDoesNotThrow(params::validate);

        params = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(),
            new TestingInvalidTaskParams()
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // `after` validation (done by TaskParams but we test that validation is triggered)
        params = new SequenceTaskParams<>(List.of(new NoOperationTaskParams<>()));
        params.after.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);

        // `using` validation
        params = new SequenceTaskParams<>(List.of(new NoOperationTaskParams<>()));
        params.using.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testFlatten() {
        SequenceTaskParams<Object> params;
        Map<String, TaskParams<Object>> flat;

        // Base sequence task
        params = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(),
            new NoOperationTaskParams<>(),
            new NoOperationTaskParams<>()
        ));

        flat = params.flatten("test");
        ScheduleParamsTester.assertValidSchedule(flat);
        assertTrue(flat.containsKey("test:1"));
        assertTrue(flat.containsKey("test:2"));
        assertTrue(flat.containsKey("test:3"));
        ScheduleParamsTester.assertPredecessor(flat, "test:1", "test:2");
        ScheduleParamsTester.assertPredecessor(flat, "test:2", "test:3");

        // Sequence in sequence
        params = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(),
            new SequenceTaskParams<>(List.of(
                new NoOperationTaskParams<>(),
                new NoOperationTaskParams<>(),
                new NoOperationTaskParams<>()
            )),
            new NoOperationTaskParams<>()
        ));

        flat = params.flatten("test");

        ScheduleParamsTester.assertValidSchedule(flat);
        assertTrue(flat.containsKey("test:1"));
        assertTrue(flat.containsKey("test:2"));
        assertTrue(flat.containsKey("test:2:1"));
        assertTrue(flat.containsKey("test:2:2"));
        assertTrue(flat.containsKey("test:2:3"));
        assertTrue(flat.containsKey("test:3"));

        ScheduleParamsTester.assertPredecessor(flat, "test:1", "test:2:1");
        ScheduleParamsTester.assertPredecessor(flat, "test:2:1", "test:2:2");
        ScheduleParamsTester.assertPredecessor(flat, "test:2:2", "test:2:3");
        ScheduleParamsTester.assertPredecessor(flat, "test:2:3", "test:3");
    }

    @Test
    public void testFlattenAfter() {
        SequenceTaskParams<Object> sequence = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(),
            new NoOperationTaskParams<>()
        ));

        TaskParams<Object> last = new NoOperationTaskParams<>();

        ScheduleTaskParams<Object> params = new ScheduleTaskParams<>(Map.of(
            "first", new NoOperationTaskParams<>(),
            "last", last,
            "other", new NoOperationTaskParams<>(),
            "sequence", sequence
        ));

        sequence.after.add("first");
        last.after.add("sequence");

        Map<String, TaskParams<Object>> flat = params.flatten("test");

        // Checks both task after sequence and sequence after task

        ScheduleParamsTester.assertValidSchedule(flat);
        ScheduleParamsTester.assertPredecessor(flat, "test:sequence:2", "test:last");
        ScheduleParamsTester.assertPredecessor(flat, "test:first", "test:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(flat, "test:other", "test:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(flat, "test:sequence:2", "test:other");
    }

    @Test
    public void testFlattenUsing() {

        TaskParams<Object> subtask = new NoOperationTaskParams<>();
        TaskParams<Object> subsubtask = new NoOperationTaskParams<>();

        // Beware: sequence order may induce unwanted dependencies

        SequenceTaskParams<Object> subsequence = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(), // sequence:2:1
            subsubtask               // sequence:2:2
        ));

        SequenceTaskParams<Object> sequence = new SequenceTaskParams<>(List.of(
            new NoOperationTaskParams<>(), // sequence:1
            subsequence,             // sequence:2
            subtask                  // sequence:3
        ));

        ScheduleTaskParams<Object> params = new ScheduleTaskParams<>(Map.of(
            "sequence", sequence,
            "task", new NoOperationTaskParams<>(),
            "othertask", new NoOperationTaskParams<>()
        ));

        subtask.after.add("task");
        sequence.using.add("task");
        subsubtask.after.add("task");
        subsequence.using.add("task");

        Map<String, TaskParams<Object>> additional = assertDoesNotThrow(() -> params.flatten("main"));

        ScheduleParamsTester.assertValidSchedule(additional);
        ScheduleParamsTester.assertPredecessor(additional, "main:task", "main:sequence:3", "main:sequence:2:2");
        ScheduleParamsTester.assertNotPredecessor(additional, "main:task", "main:sequence:2:1", "main:sequence:1");
        ScheduleParamsTester.assertNotPredecessor(additional, "main:sequence:3", "main:sequence:2:2", "main:sequence:3");
    }
}
