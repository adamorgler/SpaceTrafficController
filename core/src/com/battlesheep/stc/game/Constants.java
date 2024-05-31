package com.battlesheep.stc.game;

public class Constants {

    // orbital bodies
    private final static double M_EARTH = 5.98e24; // mass of Earth in kg
    private final static double RADIUS_EARTH = 6.371e6; // radius of Earth in meters
    private final static double EARTH_ATMOSPHERE = 100e3; // earth atmosphere height in meters
    private final static int EARTH_DAY_LENGTH = 86400; // number of seconds in each day

    // physics constants
    public final static double G = 6.673e-11; // gravitational constant

    public static double getMassCentralBody() {
        return M_EARTH;
    }

    public static double getRadiusCentralBody() {
        return RADIUS_EARTH;
    }

    public static double getAtmosphereCentralBody() {
        return EARTH_ATMOSPHERE;
    }

    public static int getDayLengthCentralBody() {
        return EARTH_DAY_LENGTH;
    }

    public static double[] polarToCartesian(double r, double a) {
        double x = r * Math.cos(a);
        double y = r * Math.sin(a);
        return new double[]{x, y};
    }

    public static double[] cartesianToPolar(double x, double y) {
        double r = Math.sqrt((x * x) + (y * y));
        double a = Math.atan(y / x);
        if (x < 0) a += Math.PI;
        return new double[]{r, a};
    }

    public static double distanceBetween(double x1, double y1, double x2, double y2) {
        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public static double distanceBetween(Orbit o1, Orbit o2) {
        return distanceBetween(o1.getXPos(), o1.getYPos(), o2.getXPos(), o2.getYPos());
    }
}
