package com.ignfab.minalac.generator.parameters.processors.post;

import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.processors.post.TestingPostProcessor;

/**
 * A {@code PostProcessorParams} for testing purposes.
 */
public class TestingPostProcessorParams extends PostProcessorParams {
    private final String mark;
    private final boolean valid;

    /**
     * A valid testing post-processor params.
     */
    public static final TestingPostProcessorParams VALID = new TestingPostProcessorParams();
    /**
     * An invalid testing post-processor params.
     */
    public static final TestingPostProcessorParams INVALID = new TestingPostProcessorParams(false);

    public TestingPostProcessorParams(String mark, boolean valid) {
        this.mark = mark;
        this.valid = valid;
    }

    public TestingPostProcessorParams(String mark) {
        this(mark, true);
    }

    public TestingPostProcessorParams(boolean valid) {
        this(null, valid);
    }

    public TestingPostProcessorParams() {
        this(null, true);
    }

    @Override
    public void validate() {
        if (!valid)
            throw new IllegalArgumentException();
    }

    @Override
    public PostProcessor<?, ?> create() {
        return new TestingPostProcessor(mark);
    }
}
