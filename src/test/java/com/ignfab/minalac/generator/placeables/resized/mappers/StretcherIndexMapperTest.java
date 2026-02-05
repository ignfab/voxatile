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

public class StretcherIndexMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 2, -1), "negative length");
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, -1, 1), "negative lengthAtRest");
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 3, 1), "Squeezed more than once");
    }

    @Test
    public void testPlaceable() {
        IndexMapper same = assertDoesNotThrow(() -> (new StretcherIndexMapper(1, 3, 3)));
        assertEquals(new IndexMapper.PlaceableIndex(0, 0), same.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), same.placeable(1));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), same.placeable(2));

        IndexMapper stretched = assertDoesNotThrow(() -> (new StretcherIndexMapper(1, 3, 5)));
        assertEquals(new IndexMapper.PlaceableIndex(0, 0), stretched.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), stretched.placeable(1));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), stretched.placeable(2));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), stretched.placeable(3));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), stretched.placeable(4));

        IndexMapper squeezedFirst = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 3, 2)));
        assertEquals(new IndexMapper.PlaceableIndex(0, 1), squeezedFirst.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), squeezedFirst.placeable(1));

        IndexMapper squeezedSecond = assertDoesNotThrow(() -> (new StretcherIndexMapper(1, 3, 2)));
        assertEquals(new IndexMapper.PlaceableIndex(0, 0), squeezedSecond.placeable(0));
        assertEquals(new IndexMapper.PlaceableIndex(0, 2), squeezedSecond.placeable(1));

        // TODO-7 : Cas d'un axe de taille nulle (length = 0 ou lengthAtRest = 0)
        throw new RuntimeException("Faire TODO-7");
    }

    @Test
    public void testStructuresAndSize() {
        // With a null lengthAtRest means that it not stretchable by any means
        IndexMapper empty = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 0)));
        assertEquals(0, empty.structures().size());
        assertEmpty(empty.structures().iterator());
        assertEquals(0, empty.size());

        IndexMapper emptyResized = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 2)));
        assertEquals(0, emptyResized.structures().size());
        assertEmpty(emptyResized.structures().iterator());
        assertEquals(0, emptyResized.size()); // To ensure it is not the asked length instead

        // All resulting structure index will be (0, lengthAtRest) since it is meant to store the intrinsic size of the underlying structure
        // Size of the mapper is the asked length
        IndexMapper same = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 3, 3)));
        assertEquals(1, same.structures().size());
        assertBrowsesAllOnce(List.of(new IndexMapper.StructureIndex(0, 3)), same.structures().iterator());
        assertEquals(3, same.size());

        IndexMapper stretched = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 3, 5)));
        assertEquals(1, stretched.structures().size());
        assertBrowsesAllOnce(List.of(new IndexMapper.StructureIndex(0, 3)), stretched.structures().iterator());
        assertEquals(5, stretched.size());

        IndexMapper squeezed = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 3, 2)));
        assertEquals(1, squeezed.structures().size());
        assertBrowsesAllOnce(List.of(new IndexMapper.StructureIndex(0, 3)), squeezed.structures().iterator());
        assertEquals(2, squeezed.size());
    }
}
