package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.tasks.ScheduleParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTaskParamsTest {

    @Test
    public void testValidate() {
        ScheduleTaskParams<Object> params;

        // Subtasks validation
        params = new ScheduleTaskParams<>(Map.of(
            "a", new NoOperationTaskParams<>(),
            "b", new NoOperationTaskParams<>()
        ));
        assertDoesNotThrow(params::validate);

        params = new ScheduleTaskParams<>(Map.of(
            "a", new NoOperationTaskParams<>(),
            "b", new TestingInvalidTaskParams(),
            "c", new NoOperationTaskParams<>()
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // `after` validation (done by TileTaskParams but we test that validation is triggered)
        params = new ScheduleTaskParams<>(Map.of("a", new NoOperationTaskParams<>()));
        params.after.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);

        // `using` validation
        params = new ScheduleTaskParams<>(Map.of("a", new NoOperationTaskParams<>()));
        params.using.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testFlatten() {
        ScheduleTaskParams<Object> params;
        Map<String, TaskParams<Object>> flat;

        // Base schedule task
        params = new ScheduleTaskParams<>(Map.of(
            "a", new NoOperationTaskParams<>(),
            "b", new NoOperationTaskParams<>(),
            "c", new NoOperationTaskParams<>()
        ));

        flat = params.flatten("test");
        ScheduleParamsTester.assertValidSchedule(flat);
        assertTrue(flat.containsKey("test:a"));
        assertTrue(flat.containsKey("test:b"));
        assertTrue(flat.containsKey("test:c"));

        // Schedule in schedule
        params = new ScheduleTaskParams<>(Map.of(
            "A", new NoOperationTaskParams<>(),
            "B", new NoOperationTaskParams<>(),
            "S", new ScheduleTaskParams<>(Map.of(
                "X", new NoOperationTaskParams<>(),
                "Y", new NoOperationTaskParams<>()
            ))
        ));

        flat = params.flatten("test");

        ScheduleParamsTester.assertValidSchedule(flat);
        assertTrue(flat.containsKey("test:A"));
        assertTrue(flat.containsKey("test:B"));
        assertTrue(flat.containsKey("test:S:X"));
        assertTrue(flat.containsKey("test:S:Y"));
    }

    @DisplayName("Test all task in a schedule runs before tasks that are after schedule")
    @Test
    public void testFlattenAfterSchedule() {

        TaskParams<Object> taskB = new NoOperationTaskParams<>();
        TaskParams<Object> taskC = new NoOperationTaskParams<>();

        ScheduleTaskParams<Object> params = new ScheduleTaskParams<>(Map.of(
            "S", new ScheduleTaskParams<>(Map.of(
                "X", new NoOperationTaskParams<>(),
                "Y", new NoOperationTaskParams<>()
            )),
            "A", new NoOperationTaskParams<>(),
            "B", taskB,
            "C", taskC
        ));

        taskB.after.add("S");
        taskC.after.add("S");

        Map<String, TaskParams<Object>> schedule = params.flatten("T");

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:S:X", "T:A");
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:S:Y", "T:A");
        ScheduleParamsTester.assertPredecessor(schedule, "T:S:X", "T:B", "T:B");
        ScheduleParamsTester.assertPredecessor(schedule, "T:S:X", "T:C", "T:C");
    }

    @DisplayName("Test all task in a schedule runs after schedule afters")
    @Test
    public void testFlattenScheduleAfter() {

        ScheduleTaskParams<Object> taskS = new ScheduleTaskParams<>(Map.of(
            "X", new NoOperationTaskParams<>(),
            "Y", new NoOperationTaskParams<>()
        ));
        ScheduleTaskParams<Object> params = new ScheduleTaskParams<>(Map.of(
                "S", taskS,
                "A", new NoOperationTaskParams<>(),
                "B", new NoOperationTaskParams<>(),
                "C", new NoOperationTaskParams<>()
        ));

        taskS.after.add("A");
        taskS.after.add("B");

        Map<String, TaskParams<Object>> schedule = params.flatten("T");

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertPredecessor(schedule, "T:A", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertPredecessor(schedule, "T:B", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:C", "T:S:X", "T:S:Y");
    }

    @DisplayName("Verify a subtask cannot depend on a main task")
    @Test
    public void testFlattenNotUsing() {
        TaskParams<Object> subtask = new NoOperationTaskParams<Object>();

        ScheduleTaskParams<Object> main = new ScheduleTaskParams<>(
            Map.of(
                "schedule", new ScheduleTaskParams<>(Map.of("task", subtask)),
                "maintask", new NoOperationTaskParams<>()
            )
        );

        subtask.after.add("maintask");
        assertThrows(IllegalArgumentException.class, () -> main.flatten("main"));
    }

    @Test
    public void testFlattenUsing() {
        // For these tests, we use a "main" ScheduleTaskParams in which we test "tested" ScheduleTaskParams

        TaskParams<Object> subtask = new NoOperationTaskParams<>();
        TaskParams<Object> subsubtask = new NoOperationTaskParams<>();

        ScheduleTaskParams<Object> subschedule = new ScheduleTaskParams<>(Map.of("subsubtask", subsubtask));

        ScheduleTaskParams<Object> schedule = new ScheduleTaskParams<>(Map.of(
            "subtask", subtask,
            "othersubtask", new NoOperationTaskParams<>(),
            "subschedule", subschedule
        ));

        ScheduleTaskParams<Object> main = new ScheduleTaskParams<>(Map.of(
            "schedule", schedule,
            "task", new NoOperationTaskParams<>(),
            "othertask", new NoOperationTaskParams<>()
        ));

        subtask.after.add("task");
        schedule.using.add("task");
        subsubtask.after.add("task");
        subsubtask.after.add("othersubtask");
        subschedule.using.add("task");
        subschedule.using.add("othersubtask");

        /* Test schedule dependencies:

        main      ┊  schedule      ┊  subschedule
        ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
        task      ┊  othersubtask  ┊
          │       ┊    │           ┊
          ├────────────┴────────────> subsubtask
          │       ┊                ┊
          └────────> subtask       ┊
                  ┊                ┊
        othertask ┊                ┊
                  ┊                ┊

        */

        Map<String, TaskParams<Object>> result = assertDoesNotThrow(() -> main.flatten("main"));

        ScheduleParamsTester.assertValidSchedule(result);
        ScheduleParamsTester.assertPredecessor(result, "main:task", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertPredecessor(result, "main:schedule:othersubtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:othertask", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:task", "main:schedule:othersubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
    }
}
