package com.ignfab.minalac.generator.parameters.tasks;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.ScheduleTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.tile.TileTaskParams;

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
    }

    @Test
    public void testCreateAdditionalTaskParams() {
        ScheduleTaskParams params;
        Map<String, TileTaskParams> additional;

        // Base schedule task
        params = new ScheduleTaskParams(Map.of(
            "a", new TestingTaskParams(),
            "b", new TestingTaskParams(),
            "c", new TestingTaskParams()
        ));

        additional = params.createAdditionalTaskParams("test");
        ScheduleParamsTester.assertValidSchedule(additional);
        assertTrue(additional.containsKey("test:a"));
        assertTrue(additional.containsKey("test:b"));
        assertTrue(additional.containsKey("test:c"));

        // Schedule in schedule
        params = new ScheduleTaskParams(Map.of(
            "A", new TestingTaskParams(),
            "B", new TestingTaskParams(),
            "S", new ScheduleTaskParams(Map.of(
                "X", new TestingTaskParams(),
                "Y", new TestingTaskParams()
            ))
        ));

        additional = params.createAdditionalTaskParams("test");

        ScheduleParamsTester.assertValidSchedule(additional);
        assertTrue(additional.containsKey("test:A"));
        assertTrue(additional.containsKey("test:B"));
        assertTrue(additional.containsKey("test:S:X"));
        assertTrue(additional.containsKey("test:S:Y"));
    }

    @DisplayName("Test all task in a schedule runs before tasks that are after schedule")
    @Test
    public void testCreateAdditionalTaskParamsAfterSchedule() {

        TileTaskParams taskB = new TestingTaskParams();
        TileTaskParams taskC = new TestingTaskParams();

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

        Map<String, TileTaskParams> schedule = params.createAdditionalTaskParams("T");

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:S:X", "T:A");
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:S:Y", "T:A");
        ScheduleParamsTester.assertPredecessor(schedule, "T:S:X", "T:B", "T:B");
        ScheduleParamsTester.assertPredecessor(schedule, "T:S:X", "T:C", "T:C");
    }

    @DisplayName("Test all task in a schedule runs after schedule afters")
    @Test
    public void testCreateAdditionalTaskParamsScheduleAfter() {

        ScheduleTaskParams taskS = new ScheduleTaskParams(Map.of(
            "X", new TestingTaskParams(),
            "Y", new TestingTaskParams()
        ));
        ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
                "S", taskS,
                "A", new TestingTaskParams(),
                "B", new TestingTaskParams(),
                "C", new TestingTaskParams()
        ));

        taskS.after.add("A");
        taskS.after.add("B");

        Map<String, TileTaskParams> schedule = params.createAdditionalTaskParams("T");

        ScheduleParamsTester.assertValidSchedule(schedule);
        ScheduleParamsTester.assertPredecessor(schedule, "T:A", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertPredecessor(schedule, "T:B", "T:S:X", "T:S:Y");
        ScheduleParamsTester.assertNotPredecessor(schedule, "T:C", "T:S:X", "T:S:Y");
    }

    @DisplayName("Verify a subtask cannot depend on a main task")
    @Test
    public void testCreateAdditionalTaskParamsNotUsing() {
        TestingTaskParams subtask = new TestingTaskParams();

        ScheduleTaskParams main = new ScheduleTaskParams(
            Map.of(
                "schedule", new ScheduleTaskParams(Map.of("task", subtask)),
                "maintask", new TestingTaskParams()
            )
        );

        subtask.after.add("maintask");
        assertThrows(IllegalArgumentException.class, () -> main.createAdditionalTaskParams("main"));
    }

    @Test
    public void testCreateAdditionalTaskParamsUsing() {
        // For these tests, we use a "main" ScheduleTaskParams in which we test "tested" ScheduleTaskParams

        TestingTaskParams subtask = new TestingTaskParams();
        TestingTaskParams subsubtask = new TestingTaskParams();

        ScheduleTaskParams subschedule = new ScheduleTaskParams(Map.of("subsubtask", subsubtask));

        ScheduleTaskParams schedule = new ScheduleTaskParams(Map.of(
            "subtask", subtask,
            "othersubtask", new TestingTaskParams(),
            "subschedule", subschedule
        ));

        ScheduleTaskParams main = new ScheduleTaskParams(Map.of(
            "schedule", schedule,
            "task", new TestingTaskParams(),
            "othertask", new TestingTaskParams()
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

        Map<String, TileTaskParams> result = assertDoesNotThrow(() -> main.createAdditionalTaskParams("main"));

        ScheduleParamsTester.assertValidSchedule(result);
        ScheduleParamsTester.assertPredecessor(result, "main:task", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertPredecessor(result, "main:schedule:othersubtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:othertask", "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:task", "main:schedule:othersubtask");
        ScheduleParamsTester.assertNotPredecessor(result, "main:schedule:subtask", "main:schedule:subschedule:subsubtask");
    }

    @Test
    public void testModelSelection() {
        // Just ensure model selection merging has not been forgotten
        TestingTaskParams foo = new TestingTaskParams();
        TestingTaskParams bar = new TestingTaskParams();
        foo.models.type = "foo";

        ScheduleTaskParams params = new ScheduleTaskParams(Map.of(
            "foo", foo,
            "bar", bar
        ));
        params.models.type = "bar";

        Map<String, TileTaskParams> additional = params.createAdditionalTaskParams("test");
        TestingTaskParams testFoo = assertInstanceOf(TestingTaskParams.class, additional.get("test:foo"));
        assertEquals(ModelSelection.NONE, testFoo.models.create());
        TestingTaskParams testBar = assertInstanceOf(TestingTaskParams.class, additional.get("test:bar"));
        assertEquals("bar", testBar.models.type);
    }

}
