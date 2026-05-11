package com.ignfab.minalac.generator.inputs;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;


public class TestingProviderTest {
    @Test
    public void testProvide() {
        List<String> data = List.of("one", "two", "three", "four");

        TestingProvider provider;
        Provider.Result<String> result;
        boolean next;

        // Test not failing behavior

        provider = new TestingProvider(null, data);
        result = provider.provide(WorldBBox3d.EMPTY);

        for (int i = 0; i < data.size(); i++) {
            next = assertDoesNotThrow(result::hasNext, "Not failing provider Result::hasNext should not fail for item number %d".formatted(i));
            assertTrue(next, "Not failing provider should have more than %d items".formatted(i));
            assertDoesNotThrow(result::next, "Not failing provider should provide item %d".formatted(i));
        }
        next = assertDoesNotThrow(result::hasNext, "Result::hasNext should not fail after last item");
        assertFalse(next, "Not failing provider should not provide more than %d results".formatted(data.size()));

        // Test failing behavior

        int failAfter = 3;

        provider = new TestingProvider(null, data, failAfter, 2);

        int t;

        // First and second try: should fail after failAfter results
        for (t = 0; t < 2; t++) {
            result = provider.provide(WorldBBox3d.EMPTY);

            for (int i = 0; i < failAfter; i++) {
                next = assertDoesNotThrow(result::hasNext, "Try %d, provider Result::hasNext should not fail for item number %d".formatted(t, i));
                assertTrue(next, "Try %d, provider should return more than %d items".formatted(t, i));
                assertDoesNotThrow(result::next, "Try %d, provider should not fail for item number %d".formatted(t, i));
            }

            next = assertDoesNotThrow(result::hasNext, "Try %d, provider Result::hasNext should not fail for item number %d".formatted(t, failAfter));
            assertTrue(next, "Try %d, provider should have at least %d results".formatted(t, failAfter));
            assertThrows(RetryableException.class, result::next, "Try %d, provider should throw RetryableException for item number %d".formatted(t, failAfter));
        }

        // Third try: should succeed
        result = provider.provide(WorldBBox3d.EMPTY);

        for (int i = 0; i < data.size(); i++) {
            next = assertDoesNotThrow(result::hasNext, "Try %d, Result::hasNext should not fail for item number %d".formatted(t, i));
            assertTrue(next, "Try %d, provider should return more than %d items".formatted(t, i));
            assertDoesNotThrow(result::next, "Try %d, provider should not fail for item number %d".formatted(t, i));
        }
    }
}
