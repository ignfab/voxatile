package com.ignfab.minalac.generator.placeables.resized.mappers;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.assertBrowsesAllOnce;
import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.assertEmpty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LengthIndexMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new LengthIndexMapper(2, 1, -5));
        assertThrows(IllegalArgumentException.class, () -> new LengthIndexMapper(-2, 1, 5));
        assertDoesNotThrow(() -> new LengthIndexMapper(1, 2, 0));
        assertDoesNotThrow(() -> new LengthIndexMapper(1, 2, 7));
    }

    @Test
    public void testPlaceable() {
        // TODO-9 : Cas d'un axe de taille nulle et sous segment taille nulle
        IndexMapper empty = assertDoesNotThrow(() -> new LengthIndexMapper(0));
        assertThrows(IndexOutOfBoundsException.class, () -> empty.placeable(0));

        IndexMapper notEmpty = assertDoesNotThrow(() -> new LengthIndexMapper(3));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.placeable(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.placeable(3));

        assertEquals(new IndexMapper.PlaceableIndex(0, 0), notEmpty.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), notEmpty.placeable(1));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), notEmpty.placeable(2));

        IndexMapper multiple = assertDoesNotThrow(() -> new LengthIndexMapper(3, 0, 1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> multiple.placeable(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> multiple.placeable(6));

        // First segment (length 3)
        assertEquals(new IndexMapper.PlaceableIndex(0, 0), multiple.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), multiple.placeable(1));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), multiple.placeable(2));

        // Third segment (length 1)
        assertEquals(new IndexMapper.PlaceableIndex(2, 0), multiple.placeable(3));

        // Fourth segment (length 0)
        assertEquals(new IndexMapper.PlaceableIndex(3, 0), multiple.placeable(4));
        assertEquals(new IndexMapper.PlaceableIndex(3, 1), multiple.placeable(5));

        throw new RuntimeException("Faire TODO-9");
    }

    @Test
    public void testStructures() {
        Collection<IndexMapper.StructureIndex> empty = assertDoesNotThrow(() -> (new LengthIndexMapper().structures()));
        assertEquals(0, empty.size());
        assertEmpty(empty.iterator());

        Collection<IndexMapper.StructureIndex> notEmpty = assertDoesNotThrow(() -> (new LengthIndexMapper(3, 1, 2).structures()));
        assertEquals(3, notEmpty.size());
        assertBrowsesAllOnce(
            List.of(
                new IndexMapper.StructureIndex(0, 3),
                new IndexMapper.StructureIndex(1, 1),
                new IndexMapper.StructureIndex(2, 2)
            ), notEmpty.iterator());

        notEmpty = assertDoesNotThrow(() -> (new LengthIndexMapper(0, 1, 2).structures()));
        assertEquals(3, notEmpty.size());
        assertBrowsesAllOnce(
            List.of(
                new IndexMapper.StructureIndex(0, 0),
                new IndexMapper.StructureIndex(1, 1),
                new IndexMapper.StructureIndex(2, 2)
            ), notEmpty.iterator());
    }

    @Test
    public void testSize() {
        IndexMapper empty = assertDoesNotThrow(() -> new LengthIndexMapper(0));
        assertEquals(0, empty.size());

        IndexMapper notEmpty = assertDoesNotThrow(() -> new LengthIndexMapper(3, 1, 5));
        assertEquals(9, notEmpty.size());
    }
}
