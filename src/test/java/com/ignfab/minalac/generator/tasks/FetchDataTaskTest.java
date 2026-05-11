package com.ignfab.minalac.generator.tasks;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.inputs.TestingProvider;
import com.ignfab.minalac.generator.processors.TestingProcessor;
import com.ignfab.minalac.generator.processors.post.IdentityPostProcessor;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class FetchDataTaskTest {
    private static List<String> data = List.of("one", "two", "three", "four");

    @Test
    @DisplayName("Test retry works")
    public void testRetry() {
        TestingProvider provider = new TestingProvider(null, data, 2, 3); // Three fails after two models
        TestingProcessor processor = new TestingProcessor();
        FetchDataTask task = new FetchDataTask("test", provider, processor, IdentityPostProcessor.INSTANCE, 10, Duration.ZERO);

        TestingGenerationTile tile = new TestingGenerationTile(WorldBBox3d.EMPTY);
        assertDoesNotThrow(() -> task.run(tile));

        assertEquals(10, processor.processed()); // 3 fails x 2 models + 1 success x 4 models
        assertEquals(4, tile.models().getByType("test").size());
    }

    @Test
    @DisplayName("Test task fails if not enough retries")
    public void testRetryFail() {
        TestingProvider provider = new TestingProvider(null, data, 2, 3);
        TestingProcessor processor = new TestingProcessor();
        FetchDataTask task = new FetchDataTask("test", provider, processor, IdentityPostProcessor.INSTANCE, 2, Duration.ZERO);

        TestingGenerationTile tile = new TestingGenerationTile(WorldBBox3d.EMPTY);
        assertThrows(RuntimeException.class, () -> task.run(tile));

        assertEquals(4, processor.processed()); // 2 fails x 2 models
        assertEquals(0, tile.models().getByType("test").size());
    }

    @Test
    @DisplayName("Test task works with delay")
    public void testRetryWithDelay() {
        FetchDataTask task = new FetchDataTask(
            "test",
            new TestingProvider(null, data, 2, 1),
            new TestingProcessor(),
            IdentityPostProcessor.INSTANCE,
            10,
            Duration.ofMillis(10)
        );

        assertDoesNotThrow(() -> task.run(new TestingGenerationTile(WorldBBox3d.EMPTY)));
    }
}
