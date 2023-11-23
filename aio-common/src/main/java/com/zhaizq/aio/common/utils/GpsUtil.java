package com.zhaizq.aio.common.utils;

public class GpsUtil {
    public static double distance(double lon1, double lat1, double lon2, double lat2) {
        double rlat1 = Math.toRadians(lat1);
        double rlat2 = Math.toRadians(lat2);
        double b = Math.toRadians(lon1) - Math.toRadians(lon2);
        return 6378137 * 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((rlat1 - rlat2) / 2), 2)
                + Math.cos(rlat1) * Math.cos(rlat2) * Math.pow(Math.sin(b / 2), 2)));
    }
}
