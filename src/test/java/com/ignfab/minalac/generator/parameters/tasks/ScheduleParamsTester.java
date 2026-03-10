package com.ignfab.minalac.generator.parameters.tasks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class ScheduleParamsTester {
    // Utility class
    private ScheduleParamsTester() {}

    private static void assertValidScheduleTask(Map<String, TaskParams> schedule, List<String> seen, String taskName) {
        if (seen.contains(taskName)) {
            String cycle = null;
            for (String name : seen) {
                if (cycle != null)
                    cycle += " > " + name;
                if (name.equals(taskName))
                    cycle = taskName;
            }
            fail("Schedule expected to have no circular dependency, one found: %s > %s".formatted(cycle, taskName));
        }

        seen.add(taskName);

        for (String name : schedule.get(taskName).after) {
            assertTrue(schedule.containsKey(name), "Task \"%s\" in \"%s\" after list expected to be in schedule".formatted(name, taskName));
            assertValidScheduleTask(schedule, new LinkedList<>(seen), name);
        }
    }

    /**
     * Asserts a schedule is valid.
     * <p>
     * This checks that:
     * <ul>
     *  <li>all "after" refers to existing tasks;</li>
     *  <li>there is no circular dependency;</li>
     * </ul>
     * @param schedule Schedule as a map of tasks indexed by their names
     */
    public static void assertValidSchedule(Map<String, TaskParams> schedule) {
        for (String name : schedule.keySet())
            assertValidScheduleTask(schedule, new LinkedList<>(), name);
    }

    // Returns true if afterName is anyhow after beforeName in schedule.
    private static boolean checkOrder(
        Map<String, TaskParams> schedule,
        String beforeName,
        String afterName
    ) {
        TaskParams after = schedule.get(afterName);
        if (after == null)
            throw new IllegalArgumentException("Task \"%s\" not in schedule".formatted(afterName));
        if (after.after.contains(beforeName))
            return true;

        for (String name : after.after)
            if (checkOrder(schedule, beforeName, name))
                return true;

        return false;
    }

    /**
     * Asserts a task is predecessor of other tasks in a given schedule.
     * The successor tasks don't necessary have the predecessor task in its "afters" but
     * there should be a chain of "afters" that ensures it runs after predecessor task.
     * <p>
     * WARNING: A schedule with circular dependencies may trigger infinite loops.
     * Please check schedule is valid before calling this method.
     *
     * @param schedule Schedule as a map of tasks indexed by their names
     * @param predecessorName Name of the task expected to run before successor
     * @param successorsNames Names of tasks expected to run after predecessor
     */
    public static void assertPredecessor(
        Map<String, TaskParams> schedule,
        String predecessorName,
        String... successorsNames
    ) {
        for (String successorName : successorsNames) {
            if (!schedule.containsKey(predecessorName))
                fail("Task \"%s\" expected to be in schedule (predecessor)".formatted(predecessorName));
            if (!schedule.containsKey(successorName))
                fail("Task \"%s\" expected to be in schedule (successor)".formatted(successorName));
            if (!checkOrder(schedule, predecessorName, successorName))
                fail("Task \"%s\" expected to be a successor of \"%s\"".formatted(successorName, predecessorName));
        }
    }

    /**
     * Asserts a task is not predecessor of other tasks in a given schedule.
     * It ensures that successor have no direct or indirect dependency to predecessor.
     * <p>
     * WARNING: A schedule with circular dependencies may trigger infinite loops.
     * Please check schedule is valid before calling this method.
     *
     * @param schedule Schedule as a map of tasks indexed by their names
     * @param predecessorName Name of the predecessor task to test
     * @param successorsNames Name of tasks expected to have no dependancy to predecessor
     */
    public static void assertNotPredecessor(
        Map<String, TaskParams> schedule,
        String predecessorName,
        String... successorsNames
    ) {
        for (String successorName : successorsNames)
            if (checkOrder(schedule, predecessorName, successorName))
                fail("Task \"%s\" not expected to be a successor of \"%s\"".formatted(successorName, predecessorName));
    }
}
