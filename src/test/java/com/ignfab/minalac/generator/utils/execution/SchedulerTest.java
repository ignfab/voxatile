package com.ignfab.minalac.generator.utils.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        scheduler = new Scheduler();
    }

    @AfterEach
    public void tearDown() {
        scheduler.shutdown();
    }

    @Test
    public void testTasksWithoutDependency() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("testWithoutDependency:a", () -> actual.add("a"));
        scheduler.schedule("testWithoutDependency:b", () -> actual.add("b"));
        scheduler.schedule("testWithoutDependency:c", () -> actual.add("c"));

        assertDoesNotThrow(() -> scheduler.run(1, TimeUnit.MINUTES));

        List<String> expected = Arrays.asList("a", "b", "c");
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected),
            () -> "List elements expected to be the same (ignoring order). Expected: " + expected + ", actual: " + actual);
    }


    @Test
    public void testTasksWithDependency() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("testWithDependency:1", () -> actual.add("1"));
        scheduler.schedule("testWithDependency:2", () -> actual.add("2"));
        scheduler.schedule("testWithDependency:3", () -> actual.add("3"));
        scheduler.addDependency("testWithDependency:2", "testWithDependency:1");
        scheduler.addDependency("testWithDependency:3", "testWithDependency:1");
        scheduler.addDependency("testWithDependency:3", "testWithDependency:2");

        assertDoesNotThrow(() -> scheduler.run(1, TimeUnit.MINUTES));

        List<String> expected = Arrays.asList("1", "2", "3");
        assertEquals(expected, actual);
    }

    @Test
    public void testResetTasks() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("testReset:x", () -> actual.add("x"));
        scheduler.schedule("testReset:y", () -> actual.add("y"));
        scheduler.schedule("testReset:z", () -> actual.add("z"));
        scheduler.addDependency("testReset:y", "testReset:x");
        scheduler.addDependency("testReset:z", "testReset:y");

        List<String> expected = Arrays.asList("x", "y", "z");

        for (int i = 1; i <= 3; i++) {
            assertDoesNotThrow(() -> scheduler.run(1, TimeUnit.MINUTES));
            assertEquals(expected, actual, "Iteration " + i);
            actual.clear();
        }
    }

    @Test
    public void testFailingTask() {
        ScheduledTask task = new ScheduledTask("testFailingTask:id", () -> {
            throw new RuntimeException("Boom!");
        });
        scheduler.schedule(task);

        TaskFailedException t = assertThrows(TaskFailedException.class, () -> scheduler.run(1, TimeUnit.MINUTES));

        assertEquals(task, t.getTask());
    }

    @Test
    public void testLongTaskTimedOut() {
        AtomicBoolean executed = new AtomicBoolean(false);
        scheduler.schedule("testLongTaskTimeout:id", () -> {
            try {
                Thread.sleep(10_000); // Completes after 10s
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
            executed.set(true); // Should never happen because timeout is 50ms
        });

        assertThrows(TimeoutException.class, () -> scheduler.run(50, TimeUnit.MILLISECONDS));
        assertFalse(executed.get());
    }

    @Test
    public void testDeadLock() {
        scheduler.schedule("testDeadLock:A", () -> {});
        scheduler.schedule("testDeadLock:B", () -> {});
        scheduler.addDependency("testDeadLock:A", "testDeadLock:B");
        scheduler.addDependency("testDeadLock:B", "testDeadLock:A");

        assertThrows(IllegalStateException.class, () -> scheduler.run(1, TimeUnit.MINUTES));
    }

    @Test
    public void testCancel() {
        ScheduledTask cancelled = new ScheduledTask("testCancel:cancelled", () -> {
            try {
                Thread.sleep(3_000); // Completes after 3s
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        });

        ScheduledTask failed = new ScheduledTask("testCancel:fail", () -> {
            try {
                Thread.sleep(500); // Completes after 0.5s
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
            throw new RuntimeException("Boom!");
        });

        ScheduledTask success = new ScheduledTask("testCancel:success", () -> {});

        scheduler.schedule(cancelled);
        scheduler.schedule(failed);
        scheduler.schedule(success);

        assertThrows(TaskFailedException.class, () -> scheduler.run(1, TimeUnit.MINUTES));
        assertEquals(ScheduledTaskState.RUNNING, cancelled.state());
        assertEquals(ScheduledTaskState.FAILED, failed.state());
        assertEquals(ScheduledTaskState.FINISHED, success.state());
    }

}
