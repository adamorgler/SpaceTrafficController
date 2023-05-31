package com.battlesheep.stc.game;

public class Constants {

    // orbital bodies
    public final static double M_EARTH = 5.98e24; // mass of Earth in kg
    public final static double RADIUS_EARTH = 6.371e6; // radius of Earth in meters
    public final static double EARTH_ATMOSPHERE = 100e3; // earth atmosphere height in meters
    public final static int EARTH_DAY_LENGTH = 86400; // number of seconds in each day

    // physics constants
    public final static double G = 6.673e-11; // gravitational constant

    public static double[] PolarToCartesian(double r, double a) {
        double x = r * Math.cos(a);
        double y = r * Math.sin(a);
        return new double[]{x, y};
    }

    public static double[] CartesianToPolar(double x, double y) {
        double r = Math.sqrt((x * x) + (y * y));
        double a = Math.atan(y / x);
        return new double[]{r, a};
    }

    public static double DistanceBetween(double x1, double y1, double x2, double y2) {
        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }
}
