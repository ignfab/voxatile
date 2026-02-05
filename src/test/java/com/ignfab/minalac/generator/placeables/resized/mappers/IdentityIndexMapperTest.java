package com.ignfab.minalac.generator.placeables.resized.mappers;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class IdentityIndexMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new IdentityIndexMapper(-1));
    }

    @Test
    public void testPlaceable() {
        // TODO-8 : Cas d'un axe de taille nulle
        IndexMapper empty = assertDoesNotThrow(() -> new IdentityIndexMapper(0));
        assertThrows(IndexOutOfBoundsException.class, () -> empty.placeable(0));

        IndexMapper notEmpty = assertDoesNotThrow(() -> new IdentityIndexMapper(3));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.placeable(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.placeable(3));

        assertEquals(new IndexMapper.PlaceableIndex(0, 0), notEmpty.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), notEmpty.placeable(1));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), notEmpty.placeable(2));

        throw new RuntimeException("Faire TODO-8");
    }

    @Test
    public void testStructures() {
        Collection<IndexMapper.StructureIndex> empty = assertDoesNotThrow(() -> (new IdentityIndexMapper(0).structures()));
        assertEquals(0, empty.size());
        assertEmpty(empty.iterator());

        Collection<IndexMapper.StructureIndex> notEmpty = assertDoesNotThrow(() -> (new IdentityIndexMapper(7).structures()));
        assertEquals(1, notEmpty.size());
        assertBrowsesAllOnce(List.of(new IndexMapper.StructureIndex(0, 7)), notEmpty.iterator());
    }

    @Test
    public void testSize() {
        IndexMapper empty = assertDoesNotThrow(() -> new IdentityIndexMapper(0));
        assertEquals(0, empty.size());

        IndexMapper notEmpty = assertDoesNotThrow(() -> new IdentityIndexMapper(37));
        assertEquals(37, notEmpty.size());
    }
}
