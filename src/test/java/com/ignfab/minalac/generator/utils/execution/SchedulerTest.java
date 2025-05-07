package com.ignfab.minalac.generator.utils.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        scheduler = new Scheduler();
    }

    @Test
    public void testTasksWithoutDependency() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("a", () -> actual.add("a"));
        scheduler.schedule("b", () -> actual.add("b"));
        scheduler.schedule("c", () -> actual.add("c"));

        assertDoesNotThrow(() -> scheduler.run(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        List<String> expected = Arrays.asList("a", "b", "c");
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected),
            () -> "List elements expected to be the same (ignoring order). Expected: " + expected + ", actual: " + actual);
    }


    @Test
    public void testTasksWithDependency() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("1", () -> actual.add("1"));
        scheduler.schedule("2", () -> actual.add("2"));
        scheduler.schedule("3", () -> actual.add("3"));
        scheduler.addDependency("2", "1");
        scheduler.addDependency("3", "1");
        scheduler.addDependency("3", "2");

        assertDoesNotThrow(() -> scheduler.run(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        List<String> expected = Arrays.asList("1", "2", "3");
        assertEquals(expected, actual);
    }

    @Test
    public void testResetTasks() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("x", () -> actual.add("x"));
        scheduler.schedule("y", () -> actual.add("y"));
        scheduler.schedule("z", () -> actual.add("z"));
        scheduler.addDependency("y", "x");
        scheduler.addDependency("z", "y");

        List<String> expected = Arrays.asList("x", "y", "z");

        for (int i = 1; i <= 3; i++) {
            assertDoesNotThrow(() -> scheduler.run(50, TimeUnit.MILLISECONDS));
            assertEquals(expected, actual, "Iteration " + i);
            actual.clear();
        }

        scheduler.shutdown();
    }

    @Test
    public void testFailingTask() {
        ScheduledTask task = new ScheduledTask("id", () -> {
            throw new RuntimeException("Boom!");
        });
        scheduler.schedule(task);

        TaskFailedException e = assertThrows(TaskFailedException.class, () -> scheduler.run(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        assertEquals(task, e.getTask());
    }

    @Test
    public void testLongTaskTimedOut() {
        AtomicBoolean executed = new AtomicBoolean(false);
        scheduler.schedule("id", () -> {
            try {
                Thread.sleep(10_000); // Completes after 10s
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
            executed.set(true); // Should never happen because timeout is 50ms
        });

        assertDoesNotThrow(() -> scheduler.run(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        assertFalse(executed.get());
    }
}
