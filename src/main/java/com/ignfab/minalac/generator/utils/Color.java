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

    public static record HSV(double hue, double saturation, double luminance) {};

    public HSV toHSV() {
        int value = Math.max(Math.max(red, green), blue);
        int delta = value - Math.min(Math.min(red, green), blue);
        double hue = 0;
        if (delta > 0) {
            if (value == red)
                hue = 1.0 + (green - blue) / delta;
            else if (value == green)
                hue = 3.0 + (blue -red) / delta;
            else /* (value == blue) */
                hue = 5.0 + (red - green) / delta;
        }
        // hue goes from 0.0 to 6.0 (0.0 and 6.0 are same hue)
        // saturation goes from 0.0 to 1.0
        // value goes from 0.0 to 1.0
        return new HSV(hue, value == 0 ? 0 : (double) delta / value, (double) value / 255);
    }

    private static double fn(double n) { return n * n; }

    public double distance(Color other) {
        HSV hsv1 = toHSV();
        HSV hsv2 = other.toHSV();

        double hdist = Math.abs(hsv1.hue() - hsv2.hue());
        hdist = Math.min(hdist, 6.0 - hdist) / 6.0; // From 0.0 (equal) to 1.0 (opposite)

        return
            (fn(hdist) - 0.5) * hsv1.saturation() * hsv2.saturation() + // Here we take hue in account depending of how much both colors are saturated.
            fn(hsv1.saturation() - hsv2.saturation()) +
            fn(hsv1.luminance() - hsv2.luminance());
        /*
          To apply factors on hue/saturation/luminance sensibility:

            fhue * (fn(hdist) - 0.5) * hsv1.saturation() * hsv2.saturation() + // Here we take hue in account depending of how much both colors are saturated.
            fsat * (fn(hsv1.saturation() - hsv2.saturation()) - 0.5) +
            flum * (fn(hsv1.luminance() - hsv2.luminance()) - 0.5);
        */


        // TODO : A better formula must exist!
//        return fn(red - other.red) + fn(blue - other.blue) + fn(green - other.green);
    }
}
