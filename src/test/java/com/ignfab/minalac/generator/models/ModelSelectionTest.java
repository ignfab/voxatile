package com.ignfab.minalac.generator.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class ModelSelectionTest {

    @Test
    public void testIterator() {
        TestingGenerationTile tile = new TestingGenerationTile(WorldBBox3d.EMPTY);

        Model modelA = new TestingModel("A", Map.of("a", 1));
        Model modelB = new TestingModel("B", Map.of("b", 2));
        Model modelC = new TestingModel("C");

        tile.models().add("X", List.of(modelA, modelB));
        tile.models().add("Y", List.of(modelA, modelC));
        tile.models().add("X", List.of(modelA));

        assertBrowsesAllOnce(Arrays.asList(modelA, modelA, modelB), new ModelSelection("X", null).forTile(tile));

        assertBrowsesAllOnce(Arrays.asList(modelA, modelA), new ModelSelection("X", new ModelFilterHasMetadata("a")).forTile(tile));

        assertBrowsesAllOnce(Arrays.asList(modelA, modelC), new ModelSelection("Y", null).forTile(tile));

        assertEmpty(new ModelSelection("Y", new ModelFilterHasMetadata("b")).forTile(tile));

        assertEmpty(new ModelSelection("Z", null).forTile(tile));
    }
}
