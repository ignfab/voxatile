package com.ignfab.minalac.generator.inputs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedURLTest {

    @Test
    void testBuilder() {
        assertDoesNotThrow(assertDoesNotThrow(() -> ParameterizedURL.base("https://ign.fr"))::build);
    }

    @Test
    void testToURL() {
        ParameterizedURL url = ParameterizedURL.base("https://ign.fr").build();
        assertEquals("https://ign.fr?a=b", assertDoesNotThrow(url
            .builder()
            .parameter("a", "b")
            .build()::toURL).toString());
    }

    @Test
    void testBuildURL() {
        ParameterizedURL url = ParameterizedURL.base("https://ign.fr").build();
        assertEquals("https://ign.fr", assertDoesNotThrow(() ->
            url.toURL().toString()));

        assertEquals("https://ign.fr?string=%22%3F%26", assertDoesNotThrow(() ->
            url.builder().parameter("string", "\"?&").buildURL().toString()));
    }

    @Test
    void testParameter() {
        ParameterizedURL url = ParameterizedURL.base("https://ign.fr").build();
        assertEquals("https://ign.fr?a=b", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .buildURL().toString()));

        // This checks url variable has not been affected by previous test
        assertEquals("https://ign.fr", assertDoesNotThrow(() -> url.toURL().toString()));

        String result = assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("c", "d")
            .buildURL().toString());
        assertTrue(result.equals("https://ign.fr?a=b&c=d") || result.equals("https://ign.fr?c=d&a=b"));

        assertEquals("https://ign.fr?a=d", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("a", "d")
            .buildURL().toString()));

        assertEquals("https://ign.fr?a=-3", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("a", -3)
            .buildURL().toString()));
    }
}
