package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * A TileTaskParams class for testing purposes.
 */
public class TestingTaskParams extends TileTaskParams {

    /**
     * A valid task params for tests.
     */
    public static final TestingTaskParams VALID = new TestingTaskParams();

    /**
     * An invalid task params for tests.
     */
    public static final TestingTaskParams INVALID = new TestingTaskParams(null);

    private static final Task TASK = new Task();

    /**
     * A required field.
     */
    public String requiredField;

    /**
     * An optional field.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String optionalField = "defaultOptionalValue";

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param requiredField the required field.
     */
    @ConstructorProperties({"requiredField"})
    public TestingTaskParams(String requiredField) {
        this.requiredField = requiredField;
    }

    /**
     * Constructs a valid {@code TestingTaskParams} with no arguments.
     */
    public TestingTaskParams() {
        this("dummy");
    }

    @Override
    public void validate() {
        if (requiredField == null)
            throw new IllegalArgumentException();
    }

    @Override
    public TileTask create(Generation generation) {
        return TASK;
    }

    /**
     * Dummy task for tests.
     */
    static class Task implements TileTask {
        @Override
        public void run(GenerationTile tile) {}
    }
}
