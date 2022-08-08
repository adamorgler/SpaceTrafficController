package com.battlesheep.stc.game;

public class Orbiting {

    // positional data
    private double apoapsis; // highest altitude in orbit, meters
    private double periapsis; // lowest altitude in orbit, meters
    private double v; // True anomaly. The current position of craft in orbit (0-360 degrees)
    private double w; // argument of periapsis. The orientation of the ellipse. Angle from the ascending node to the periapsis.

    // game data
    private boolean selected;

    public Orbiting(double apoapsis, double periapsis, double v, double w) {
        this.apoapsis = apoapsis;
        this.periapsis = periapsis;
        this.v = v;
        this.w = w;

        selected = false;
    }

    public void step(float time) {
        double delta = getDelta(time);
        v += delta;
        if (v > 360) {
            v -= 360;
        }
        // eccentricity
    }

    public double getApoapsis() {
        return apoapsis;
    }

    public double getPeriapsis() {
        return periapsis;
    }

    public double getV() {
        return v;
    }

    public double getW() {
        return w;
    }

    public double getApogee() {
        return apoapsis + Constants.RADIUS_EARTH;
    }

    public double getPerigee() {
        return periapsis + Constants.RADIUS_EARTH;
    }

    public double getSemiMajorAxis() {
        return (float)(getApogee() + getPerigee()) / 2;
    }

    public double getSemiMinorAxis() {
        return Math.sqrt(getApogee() * getPerigee()); // semi-minor axis
    }

    public double getEccentricity() {
        return Math.sqrt(1 - (Math.pow(getSemiMinorAxis(), 2) / Math.pow(getSemiMajorAxis(), 2)));
    }

    public double getXPos() {
        return Constants.polarToCartesian(radiusFromFoci(getSemiMajorAxis(), getEccentricity(), v), Math.toRadians(v + w))[0];
    }

    public double getYPos() {
        return Constants.polarToCartesian(radiusFromFoci(getSemiMajorAxis(), getEccentricity(), v), Math.toRadians(v + w))[1];
    }

    public double getDelta(double time) {
        double a = getSemiMajorAxis();
        double b = getSemiMinorAxis();
        double e = getEccentricity();
        double r = radiusFromFoci(a, e, v);
        //double T = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / (Constants.G * Constants.M_EARTH));
        double n = 1 / Math.sqrt(Math.pow(a, 3) / (Constants.G * Constants.M_EARTH));
        double delta = (a * b * n * time) / Math.pow(r, 2);
        return Math.toDegrees(delta);
    }

    private double radiusFromFoci(double a, double e, double theta) {
        return (a * (1 - Math.pow(e, 2))) / (1 + e * Math.cos(Math.toRadians(theta)));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
