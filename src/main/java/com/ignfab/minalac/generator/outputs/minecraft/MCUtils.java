package com.ignfab.minalac.generator.outputs.minecraft;

import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;

import java.util.UUID;

/**
 * Utility class to help with some Minecraft-related code.
 */
public final class MCUtils {
    private MCUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts a {@link UUID} into an {@code int[4]}.
     * The array contains values from most to least significant bits.
     *
     * @param uuid the UUID to convert
     * @return the converted UUID as an int array
     */
    public static int[] uuidAsFourInts(UUID uuid) {
        int[] uuidInts = new int[4];
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        uuidInts[0] = (int) (most >>> 32);
        uuidInts[1] = (int) (most & 0xFFFFFFFFL);
        uuidInts[2] = (int) (least >>> 32);
        uuidInts[3] = (int) (least & 0xFFFFFFFFL);
        return uuidInts;
    }

    /**
     * Converts a raw {@code (x, y, z)} tuple into an NBT list.
     *
     * @param x the x-component value
     * @param y the y-component value
     * @param z the z-component value
     * @return the NBT list with all three values
     */
    // In-Game coords
    public static ListTag<DoubleTag> nbtPos(double x, double y, double z) {
        ListTag<DoubleTag> pos = new ListTag<>(DoubleTag.class);
        pos.addDouble(x);
        pos.addDouble(y);
        pos.addDouble(z);
        return pos;
    }
}
