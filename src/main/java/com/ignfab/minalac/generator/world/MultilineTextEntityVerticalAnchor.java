package com.ignfab.minalac.generator.world;

/**
 * Represents the vertical anchor position in multiline texts.
 * The anchor determines the exact vertical offset when placing the text.
 */
public enum MultilineTextEntityVerticalAnchor {
    /**
     * The anchor is at the bottom of the multiline text.
     * This means the last line will be at the exact location where
     * the entity is placed, and previous lines will be above.
     */
    BOTTOM,
    /**
     * The anchor is in the middle of the multiline text.
     * With an odd number of lines, this means the middle line will
     * be at the exact location where the entity is placed.
     * With an even number of lines, this means the entity will be
     * placed at a location between the two middle lines.
     */
    MIDDLE,
    /**
     * The anchor is at the top of the multiline text.
     * This means the first line will be at the exact location where
     * the entity is placed, and next lines will be below.
     */
    TOP
}
