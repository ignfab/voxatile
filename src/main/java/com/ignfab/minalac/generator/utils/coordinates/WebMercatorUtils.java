package com.ignfab.minalac.generator.utils.coordinates;

public final class WebMercatorUtils {
    private static final double MAX_LATITUDE = 85.0511287798066;

    private WebMercatorUtils() {}

    public static double fractionalX(double longitude, int zoom) {
        return (longitude + 180.0) / 360.0 * (1 << zoom);
    }

    public static double fractionalY(double latitude, int zoom) {
        double radians = Math.toRadians(Math.max(-MAX_LATITUDE, Math.min(MAX_LATITUDE, latitude)));
        return (1.0 - Math.log(Math.tan(Math.PI / 4.0 + radians / 2.0)) / Math.PI) / 2.0 * (1 << zoom);
    }

    public static int clamp(int index, int zoom) {
        return Math.min((1 << zoom) - 1, Math.max(0, index));
    }
}
