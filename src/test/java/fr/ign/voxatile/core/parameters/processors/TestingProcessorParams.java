package fr.ign.voxatile.core.parameters.processors;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.processors.Processor;
import fr.ign.voxatile.core.processors.TestingProcessor;

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
