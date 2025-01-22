package com.ignfab.minalac.generator.inputs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ParameterizedURLTest {

    @Test
    void testBuilder() {
        assertDoesNotThrow(assertDoesNotThrow(() -> ParameterizedURL.base("http://ign.fr"))::build);
    }

    @Test
    void testToURL() {
        ParameterizedURL url = ParameterizedURL.base("http://ign.fr").build();
        assertEquals("http://ign.fr?a=b", assertDoesNotThrow(url
            .builder()
            .parameter("a", "b")
            .build()::toURL).toString());
    }

    @Test
    void testBuildURL() {
        ParameterizedURL url = ParameterizedURL.base("http://ign.fr").build();
        assertEquals("http://ign.fr", assertDoesNotThrow(() ->
            url.toURL().toString()));

        assertEquals("http://ign.fr?string=%22%3F%26", assertDoesNotThrow(() ->
            url.builder().parameter("string", "\"?&").buildURL().toString()));
    }

    @Test
    void testParameter() {
        ParameterizedURL url = ParameterizedURL.base("http://ign.fr").build();
        assertEquals("http://ign.fr?a=b", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .buildURL().toString()));

        // This checks url variable has not been affected by previous test
        assertEquals("http://ign.fr", assertDoesNotThrow(() -> url.toURL().toString()));

        String result = assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("c", "d")
            .buildURL().toString());
        assertTrue(result.equals("http://ign.fr?a=b&c=d") || result.equals("http://ign.fr?c=d&a=b"));

        assertEquals("http://ign.fr?a=d", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("a", "d")
            .buildURL().toString()));

        assertEquals("http://ign.fr?a=-3", assertDoesNotThrow(() -> url
            .builder()
            .parameter("a", "b")
            .parameter("a", -3)
            .buildURL().toString()));
    }
}
