package com.ignfab.minalac.generator.modules.minecraft;

import io.github.ensgijs.nbt.tag.CompoundTag;

/**
 * Miscellaneous utility functions for Minecraft-related operations.
 */
public final class MinecraftHelpers {
    private MinecraftHelpers() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@return the given resource location with explicit namespace}
     * If the input string already has a namespace, it is returned untouched,
     * otherwise the default namespace {@code minecraft:} is prepended.
     * @param resourceLocation the resource location
     */
    public static String ensureNamespaced(String resourceLocation) {
        return resourceLocation.indexOf(':') == -1 ? "minecraft:" + resourceLocation : resourceLocation;
    }

    /**
     * Checks that the position contained in the data is equals to the given one.
     * @param data the data containing the position as {@code x}, {@code y} and {@code z} fields
     * @param x the test value of the x-coordinate
     * @param y the test value of the y-coordinate
     * @param z the test value of the z-coordinate
     * @return {@code true} if the position are the same, {@code false} otherwise
     */
    public static boolean xyzEquals(CompoundTag data, int x, int y, int z) {
        return data.getInt("x") == x && data.getInt("y") == y && data.getInt("z") == z;
    }
}
