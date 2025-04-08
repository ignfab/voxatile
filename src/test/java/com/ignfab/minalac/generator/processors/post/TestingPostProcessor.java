package com.ignfab.minalac.generator.processors.post;

import java.util.concurrent.atomic.AtomicInteger;

import com.ignfab.minalac.generator.models.Model;

import static org.junit.jupiter.api.Assertions.*;

public class TestingPostProcessor extends PostProcessor.Generic {
    private final String mark;

    private static final AtomicInteger COUNTER = new AtomicInteger();

    public TestingPostProcessor(String mark) {
        this.mark = mark == null ? "testing-" + COUNTER.incrementAndGet() : mark;
    }

    public TestingPostProcessor() {
        this(null);
    }

    @Override
    public Model process(Model model) {
        model.setMetadata(mark, true);
        return model;
    }

    @Override
    public String toString() {
        return "%s(mark=%s)".formatted(getClass().getSimpleName(), mark);
    }

    public void assertPostProcessed(Model model, String message) {
        assertTrue(model.hasMetadata(mark), prefix(message) + "model <%s> was not post-processed by <%s>".formatted(model, this));
    }

    public void assertPostProcessed(Model model) {
        assertPostProcessed(model, null);
    }

    public void assertNotPostProcessed(Model model, String message) {
        assertFalse(model.hasMetadata(mark), prefix(message) + "model <%s> was post-processed by <%s>".formatted(model, this));
    }

    public void assertNotPostProcessed(Model model) {
        assertNotPostProcessed(model, null);
    }

    private static String prefix(String message) {
        return message == null ? "" : message + " ==> ";
    }
}
