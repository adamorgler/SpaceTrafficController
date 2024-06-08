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

    public static double getOrbitalIntersectionRadiusFromA(Orbit o, double a) {
        double w = o.getW();
        return o.getRadiusAtV(a - w);
    }

    public static double[] getOrbitalPosition(Orbit o) {
        double ap = o.getApoapsis();
        double pe = o.getPeriapsis();
        double v = o.getV();
        double w = o.getW();
        return getOrbitalPosition(ap, pe, v, w);
    }

    public static double[] getOrbitalPosition(double ap, double pe, double v, double w) {
        // https://farside.ph.utexas.edu/teaching/celestial/Celestial/node30.html
        double apogee = ap + Constants.getRadiusCentralBody();
        double perigee = pe + Constants.getRadiusCentralBody();
        double a = (float)(apogee + perigee) / 2; // semi-major axis
        double b = Math.sqrt(apogee * perigee); // semi-minor axis
        double e = Math.sqrt(1 - (Math.pow(b, 2) / Math.pow(a, 2))); // eccentricity
        double r = radiusFromFoci(a, e, v);
        double[] p = Constants.polarToCartesian(r, Math.toRadians(v) + Math.toRadians(w));
        return p;
    }

    public static double radiusFromFoci(double a, double e, double theta) {
        return (a * (1 - Math.pow(e, 2))) / (1 + e * Math.cos(Math.toRadians(theta)));
    }
}
