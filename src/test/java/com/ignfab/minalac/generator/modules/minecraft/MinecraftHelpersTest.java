package com.ignfab.minalac.generator.modules.minecraft;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftHelpersTest {
    @Test
    public void testEnsureNamespaced() {
        assertEquals("minecraft:stone", MinecraftHelpers.ensureNamespaced("minecraft:stone"));
        assertEquals("minecraft:stone", MinecraftHelpers.ensureNamespaced("stone"));
        assertEquals("aether:holystone", MinecraftHelpers.ensureNamespaced("aether:holystone"));
    }

    @Test
    public void testXyzEquals() {
        assertTrue(MinecraftHelpers.xyzEquals(new CompoundTag(), 0, 0, 0));

        CompoundTag xyz = new CompoundTag();
        xyz.putInt("x", 1);
        xyz.putInt("y", 2);
        xyz.putInt("z", 3);
        assertTrue(MinecraftHelpers.xyzEquals(xyz, 1, 2, 3));
        assertFalse(MinecraftHelpers.xyzEquals(xyz, 0, 2, 3));
        assertFalse(MinecraftHelpers.xyzEquals(xyz, 1, 0, 3));
        assertFalse(MinecraftHelpers.xyzEquals(xyz, 1, 2, 0));
    }
}
