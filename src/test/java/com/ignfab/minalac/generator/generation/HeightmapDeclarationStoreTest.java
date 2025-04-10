package com.ignfab.minalac.generator.generation;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class HeightmapDeclarationStoreTest {
    @Test
    public void testAdd() {
        HeightmapDeclarationStore store = new HeightmapDeclarationStore();

        assertDoesNotThrow(() -> store.add(new HeightmapDeclaration("first", 1)));
        assertDoesNotThrow(() -> store.add(new HeightmapDeclaration("second", 2)));
        assertThrows(IllegalArgumentException.class, () -> store.add(new HeightmapDeclaration("first", 3)), "Should not be able to add a declaration with an existing name");
        assertThrows(IllegalArgumentException.class, () -> store.add(new HeightmapDeclaration(null, 4)), "Should not be able to add a declaration with a null name");
    }

    @Test
    public void testGet() {
        HeightmapDeclarationStore store = new HeightmapDeclarationStore();
        HeightmapDeclaration first = new HeightmapDeclaration("first", 1);
        HeightmapDeclaration second = new HeightmapDeclaration("second", 2);
        store.add(first);
        store.add(second);
        HeightmapDeclaration retrievedElement = assertDoesNotThrow(() -> store.get("second"));
        assertEquals(second, retrievedElement);
        assertThrows(NoSuchElementException.class, () -> store.get("foo"));
    }

    @Test
    public void testNames() {
        HeightmapDeclarationStore store = new HeightmapDeclarationStore();
        store.add(new HeightmapDeclaration("first", 1));
        store.add(new HeightmapDeclaration("second", 2));
        store.add(new HeightmapDeclaration("third", 3));

        assertBrowsesAllOnce(List.of("first", "second", "third"), store.names());
    }

    @Test
    public void testValues() {
        HeightmapDeclarationStore store = new HeightmapDeclarationStore();
        HeightmapDeclaration first = new HeightmapDeclaration("first", 1);
        HeightmapDeclaration second = new HeightmapDeclaration("second", 2);
        HeightmapDeclaration third = new HeightmapDeclaration("third", 2);
        store.add(first);
        store.add(second);
        store.add(third);

        assertBrowsesAllOnce(List.of(first, second, third), store.declarations());
    }
}
