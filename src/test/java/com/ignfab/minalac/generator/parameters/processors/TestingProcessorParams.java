package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.TestingProcessor;

public class TestingProcessorParams extends ProcessorParams {
    /**
     * An invalid TestingProcessorParams.
     */
    public static final TestingProcessorParams INVALID = new TestingProcessorParams(false);

    /**
     * A valid TestingProcessorParams.
     */
    public static final TestingProcessorParams VALID = new TestingProcessorParams(true);

    private final boolean valid;

    private TestingProcessorParams(boolean valid) {
        this.valid = valid;
    }

    /**
     * Creates a new valid {@code TestingProcessorParams}.
     */
    public TestingProcessorParams() {
        this(true);
    }

    @Override
    public void validate() {
        if (!valid)
            throw new IllegalArgumentException("Invalid test processor params");
    }

    @Override
    public Processor<?, ?> create(Generation generation) {
        return new TestingProcessor();
    }

}
