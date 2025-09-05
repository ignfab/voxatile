package com.ignfab.minalac.generator.utils;

import java.util.HashMap;
import java.util.Map;

public record Color(short red, short green, short blue) {

    private final static Map<String, Color> named = new HashMap<>() {{
        // CSS 1–2.0 color names
        put("white", Color.fromString("#FFFFFF"));
        put("silver", Color.fromString("#C0C0C0"));
        put("gray", Color.fromString("#808080"));
        put("black", Color.fromString("#000000"));
        put("red", Color.fromString("#FF0000"));
        put("maroon", Color.fromString("#800000"));
        put("yellow", Color.fromString("#FFFF00"));
        put("olive", Color.fromString("#808000"));
        put("lime", Color.fromString("#00FF00"));
        put("green", Color.fromString("#008000"));
        put("aqua", Color.fromString("#00FFFF"));
        put("teal", Color.fromString("#008080"));
        put("blue", Color.fromString("#0000FF"));
        put("navy", Color.fromString("#000080"));
        put("fuchsia", Color.fromString("#FF00FF"));
        put("purple", Color.fromString("#800080"));
    }};

    public static Color fromRGBint(int RGBint) {
        return new Color(
            (short) (RGBint >>> 16 & 0xff),
            (short) (RGBint >>> 8 & 0xff),
            (short) (RGBint & 0xff)
        );
    }

    // TODO:To be improved
    public static Color fromString(String c) {
        c = c.trim();
        if (c.startsWith("#")) {
            c = c.substring(1);
            if (c.length() == 3) {
                c = "" + c.charAt(0) + c.charAt(0) + c.charAt(1) + c.charAt(1) + c.charAt(2) + c.charAt(2);
            }
            try {
                if (c.length() == 6) {
                    return Color.fromRGBint(Integer.parseInt(c, 16 ));
                }
            } catch (NumberFormatException e) {}
            throw new IllegalArgumentException("Unable to parse color '#%s'".formatted(c));
        }

        Color result = named.get(c.toLowerCase());
        if (result != null)
            return result;

        throw new IllegalArgumentException("Unable to parse color '%s'".formatted(c));
    }

    public double distance(Color other) {
        // TODO : A better formula must exist!
        return Math.sqrt((red - other.red) *(red - other.red) + (blue - other.blue) * (blue - other.blue) + (green - other.green) * (green - other.green));
    }
}
