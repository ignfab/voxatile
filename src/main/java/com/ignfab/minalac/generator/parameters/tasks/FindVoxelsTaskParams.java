package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.tasks.FindVoxelsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link FindVoxelsTask}.
 */
public class FindVoxelsTaskParams extends TileTaskParams {
    /**
     * Models to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * {@code Placeable}s used to select which voxels to match (optional, default none).
     * When both this field and {@code except} are absent, any voxel (including air) is matched.
     * Mutually exclusive with {@code except}.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<PlaceableParams> only;

    /**
     * {@code Placeable}s used to select which voxels not to match (optional, default none).
     * When both this field and {@code only} are absent, any voxel (including air) is matched.
     * Mutually exclusive with {@code only}.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<PlaceableParams> except;

    /**
     * Specifies which voxels to find (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public FindParams find;

    /**
     * Represents voxels to find.
     */
    public static class FindParams {
        /**
         * Metadata where to store z-coordinate of lowest voxel found (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public String lowest;

        /**
         * Metadata where to store z-coordinate of highest voxel found (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public String highest;

        /**
         * Metadata where to store average z-coordinate of voxels found (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public String average;
    }

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param models models to use
     */
    @ConstructorProperties("models")
    public FindVoxelsTaskParams(ModelSelectionParams models) {
        this.models = models;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();

        if (only != null) {
            if (except != null)
                throw new IllegalArgumentException("Fields 'only' and 'except' are mutually exclusive.");
            if (only.isEmpty())
                throw new IllegalArgumentException("Field 'only' cannot be empty.");
            only.forEach(PlaceableParams::validate);
        }
        if (except != null) {
            if (except.isEmpty())
                throw new IllegalArgumentException("Field 'except' cannot be empty.");
            except.forEach(PlaceableParams::validate);
        }

        if (find.lowest == null && find.highest == null && find.average == null)
            throw new IllegalArgumentException("At least one of 'lowest', 'highest' or 'average' field is required.");
        if (find.lowest != null && find.lowest.isBlank())
            throw new IllegalArgumentException("Field 'lowest' cannot be empty or contain only whitespace.");
        if (find.highest != null && find.highest.isBlank())
            throw new IllegalArgumentException("Field 'highest' cannot be empty or contain only whitespace.");
        if (find.average != null && find.average.isBlank())
            throw new IllegalArgumentException("Field 'average' cannot be empty or contain only whitespace.");
    }

    @Override
    public TileTask create(Generation generation) {
        Predicate<Placeable> filter;
        if (only != null)
            filter = createContainsFilter(only, generation);
        else if (except != null)
            filter = createContainsFilter(except, generation).negate();
        else
            filter = v -> true;
        return new FindVoxelsTask(models.create(), filter, find.lowest, find.highest, find.average);
    }

    private static Predicate<Placeable> createContainsFilter(List<PlaceableParams> voxels, Generation generation) {
        return voxels.stream().map(params -> params.create(generation.seed())).toList()::contains;
    }
}
