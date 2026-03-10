package com.ignfab.minalac.generator.parameters.tasks;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterEmptyGeometryParams;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTaskParamsTest {

    @Test
    public void testValidate() {
        ScheduleTaskParams params;

        // Model selection validation
        params = new ScheduleTaskParams(Map.of("a", TestingTaskParams.VALID));
        assertDoesNotThrow(params::validate);

        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);

        // Subtasks validation
        params = new ScheduleTaskParams(Map.of(
            "a", TestingTaskParams.VALID,
            "b", TestingTaskParams.VALID
        ));
        assertDoesNotThrow(params::validate);

        params = new ScheduleTaskParams(Map.of(
            "a", TestingTaskParams.VALID,
            "b", TestingTaskParams.INVALID,
            "c", TestingTaskParams.VALID
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // `after` validation (done by TileTaskParams but we test that validation is triggered)
        params = new ScheduleTaskParams(Map.of("a", TestingTaskParams.VALID));
        params.after.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);

        // `using` validation
        params = new ScheduleTaskParams(Map.of("a", TestingTaskParams.VALID));
        params.using.add("a:a");
        assertThrows(IllegalArgumentException.class, params::validate);

        // Dependency validation
        TestingTaskParams subtask = new TestingTaskParams();
        ScheduleTaskParams schedule = new ScheduleTaskParams(Map.of("task", subtask));
        params = new ScheduleTaskParams(Map.of("schedule", schedule, "maintask", new TestingTaskParams()));

        // - No dependency violation (just checking test case)
        assertDoesNotThrow(params::validate);

        // - `after` not in `using`
        subtask.after.add("maintask");
        assertThrows(IllegalArgumentException.class, params::validate);

        // Dependency validation - `after` in `using`
        schedule.using.add("maintask");
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testFlatten() {
        Map<String, TaskParams> tasks;
        ScheduleTaskParams params;

            // Base schedule task
        params = new ScheduleTaskParams(Map.of(
                "a", new TestingTaskParams(),
                "b", new TestingTaskParams(),
                "c", new TestingTaskParams()
            ));

        tasks = params.flatten("test");
        ScheduleParamsTester.assertValidSchedule(tasks);
        assertTrue(tasks.containsKey("test:a"));
        assertTrue(tasks.containsKey("test:b"));
        assertTrue(tasks.containsKey("test:c"));

        params = new ScheduleTaskParams(Map.of(
                "A", new TestingTaskParams(),
                "B", new TestingTaskParams(),
                "S", new ScheduleTaskParams(Map.of(
                    "X", new TestingTaskParams(),
                    "Y", new TestingTaskParams()
                ))
            ));

        tasks = params.flatten("test");
        assertTrue(tasks.containsKey("test:A"));
        assertTrue(tasks.containsKey("test:B"));
        assertTrue(tasks.containsKey("test:S:X"));
        assertTrue(tasks.containsKey("test:S:Y"));
    }

    @DisplayName("Test all task in a schedule runs before tasks that are after schedule")
    @Test
    public void testFlattenAfterSchedule() {
        TaskParams taskB = new TestingTaskParams();
        TaskParams taskC = new TestingTaskParams();

        ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
            "S", new ScheduleTaskParams(Map.of(
                "X", new TestingTaskParams(),
                "Y", new TestingTaskParams()
            )),
            "A", new TestingTaskParams(),
            "B", taskB,
            "C", taskC
        ));

        taskB.after.add("S");
        taskC.after.add("S");

        Map<String, TaskParams> tasks = params.flatten("T");
        ScheduleParamsTester.assertValidSchedule(tasks);
        ScheduleParamsTester.assertNotPredecessor(tasks, "T:S:X", "T:A");
        ScheduleParamsTester.assertNotPredecessor(tasks, "T:S:Y", "T:A");
        ScheduleParamsTester.assertPredecessor(tasks, "T:S:X", "T:B", "T:B");
        ScheduleParamsTester.assertPredecessor(tasks, "T:S:X", "T:C", "T:C");
    }

    @DisplayName("Test all task in a schedule runs after schedule afters")
    @Test
    public void testFlattenScheduleAfter() {
        ScheduleTaskParams taskS = new ScheduleTaskParams(Map.of(
            "X", new TestingTaskParams(),
            "Y", new TestingTaskParams()
        ));

        ScheduleTaskParams schedule = new ScheduleTaskParams(Map.of(
            "S", taskS,
            "A", new TestingTaskParams(),
            "B", new TestingTaskParams(),
            "C", new TestingTaskParams()
        ));

        taskS.after.add("A");
        taskS.after.add("B");

        Map<String, TaskParams> tasks = schedule.flatten("T");
        ScheduleParamsTester.assertValidSchedule(tasks);
        ScheduleParamsTester.assertPredecessor(tasks, "T:A", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertPredecessor(tasks, "T:B", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertNotPredecessor(tasks, "T:C", "T:S:X", "T:S:Y");
    }

    @Test
    public void testFlattenUsing() {
        // For these tests, we use a "main" ScheduleTaskParams in which we test "tested" ScheduleTaskParams

        TestingTaskParams subtask = new TestingTaskParams();
        TestingTaskParams subsubtask = new TestingTaskParams();

        ScheduleTaskParams subscheduletask = new ScheduleTaskParams(Map.of("subsubtask", subsubtask));

        ScheduleTaskParams scheduletask = new ScheduleTaskParams(Map.of(
            "subtask", subtask,
            "othersubtask", new TestingTaskParams(),
            "subschedule", subscheduletask
        ));

        ScheduleTaskParams schedule = new ScheduleTaskParams(Map.of(
            "schedule", scheduletask,
            "task", new TestingTaskParams(),
            "othertask", new TestingTaskParams()
        ));

        subtask.after.add("task");
        scheduletask.using.add("task");
        subsubtask.after.add("task");
        subsubtask.after.add("othersubtask");
        subscheduletask.using.add("task");
        subscheduletask.using.add("othersubtask");

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

        Map<String, TaskParams> tasks = schedule.flatten("main");
        ScheduleParamsTester.assertValidSchedule(tasks);
        ScheduleParamsTester.assertPredecessor(tasks, "main:task", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertPredecessor(tasks, "main:schedule:othersubtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(tasks, "main:othertask", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(tasks, "main:task", "main:schedule:othersubtask");
        ScheduleParamsTester.assertNotPredecessor(tasks, "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
    }

    @Test
    public void testModelSelection() {
        // Just ensure model selection merging has not been forgotten
        TestingTaskParams foo = new TestingTaskParams();
        TestingTaskParams bar = new TestingTaskParams();
        foo.models.filter =  new ModelFilterEmptyGeometryParams();

        ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
            "foo", foo,
            "bar", bar
        ));
        params.models.type = "baz";

        Map<String, TaskParams> schedule = params.flatten("test");
        TestingTaskParams testFoo = assertInstanceOf(TestingTaskParams.class, schedule.get("test:foo"));
        assertEquals("baz", testFoo.models.type);
        assertNotNull(testFoo.models.filter);
        TestingTaskParams testBar = assertInstanceOf(TestingTaskParams.class, schedule.get("test:bar"));
        assertEquals("baz", testBar.models.type);
        assertNull(testBar.models.filter);
    }

}
