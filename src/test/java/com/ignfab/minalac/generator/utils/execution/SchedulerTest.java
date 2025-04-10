package com.ignfab.minalac.generator.utils.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.world.VoxelWorldTile;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        scheduler = new Scheduler();
    }

    @Test
    public void testTasksWithoutCondition() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("a", (VoxelWorldTile tile) -> actual.add("a"));
        scheduler.schedule("b", (VoxelWorldTile tile) -> actual.add("b"));
        scheduler.schedule("c", (VoxelWorldTile tile) -> actual.add("c"));

        scheduler.start(null);
        assertDoesNotThrow(() -> scheduler.waitUntilAllTasksFinished(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        List<String> expected = Arrays.asList("a", "b", "c");
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected),
            () -> "List elements expected to be the same (ignoring order). Expected: " + expected + ", actual: " + actual);
    }

    @Test
    public void testTasksWithCondition() {
        List<String> actual = Collections.synchronizedList(new ArrayList<>());

        scheduler.schedule("1", (VoxelWorldTile tile) -> actual.add("1"));
        scheduler.schedule("2", (VoxelWorldTile tile) -> actual.add("2"), "1");
        scheduler.schedule("3", (VoxelWorldTile tile) -> actual.add("3"), "1", "2");

        scheduler.start(null);
        assertDoesNotThrow(() -> scheduler.waitUntilAllTasksFinished(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        List<String> expected = Arrays.asList("1", "2", "3");
        assertEquals(expected, actual);
    }

    @Test
    public void testFailingTask() {
        ScheduledTask task = new ScheduledTask("id", (VoxelWorldTile tile) -> {
            throw new RuntimeException("Boom!");
        });
        scheduler.schedule(task);

        scheduler.start(null);
        TaskFailedException e = assertThrows(TaskFailedException.class, () -> scheduler.waitUntilAllTasksFinished(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        assertEquals(task, e.getTask());
    }

    @Test
    public void testLongTaskTimedOut() {
        AtomicBoolean executed = new AtomicBoolean(false);
        scheduler.schedule("id", (VoxelWorldTile tile) -> {
            try {
                Thread.sleep(10_000); // Completes after 10s
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
            executed.set(true); // Should never happen because timeout is 50ms
        });

        scheduler.start(null);
        assertDoesNotThrow(() -> scheduler.waitUntilAllTasksFinished(50, TimeUnit.MILLISECONDS));
        scheduler.shutdown();

        assertFalse(executed.get());
    }
}
