package com.battlesheep.stc.game;

import com.badlogic.gdx.math.Vector2;
import com.sun.org.apache.bcel.internal.Const;

public class Orbit {

    // positional data
    private double apoapsis; // highest altitude in orbit, meters
    private double periapsis; // lowest altitude in orbit, meters
    private double v; // True anomaly. The current position of craft in orbit (0-360 degrees)
    private double w; // argument of periapsis. The orientation of the ellipse. Angle from the ascending node to the periapsis.

    // game data
    private boolean selected;

    public Orbit(double apoapsis, double periapsis, double v, double w) {
        this.apoapsis = apoapsis;
        this.periapsis = periapsis;
        this.v = v;
        this.w = w;
    }

    public void step(float time) {
        double delta = getDelta(time);
        v += delta;
        if (v > 360) {
            v -= 360;
        }
        // eccentricity
    }

//    public void addDeltaVTangent(double dv) {
//        double r = radiusFromFoci(getSemiMajorAxis(), getEccentricity(), v);
//        Vector2 rVector = new Vector2((float) Constants.polarToCartesian(r, v)[0], (float) Constants.polarToCartesian(r, v)[1]);
//        double E = Math.atan((Math.sqrt(1 - Math.pow(getEccentricity(), 2)) * Math.sin(Math.toRadians(v))) / (1 + getEccentricity() * Math.cos(v)));
//        double velocity = getVelocity() + dv;
//        Vector2 vVector = new Vector2((float) (velocity * getSemiMajorAxis() * Math.cos(E)), (float) (velocity * getSemiMinorAxis() * Math.sin(E)));
//        float h = rVector.crs(vVector);
//        double u = Constants.M_EARTH * Constants.G;
//        double a = u / ((2 * u / r) - Math.pow(dv, 2));
//        double e = Math.sqrt(1 - (Math.pow(h, 2) / (a * u)));
//        double apogee = a * (1 + e);
//        double perigee = a * (1 - e);
//        double ap = apogee - Constants.RADIUS_EARTH;
//        double pe = perigee - Constants.RADIUS_EARTH;
//
//        this.apoapsis = ap;
//        this.periapsis = pe;
//    }

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
        return apoapsis + Constants.getRadiusCentralBody();
    }

    public double getPerigee() {
        return periapsis + Constants.getRadiusCentralBody();
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

    public double getVelocityAtV(double v) {
        double r = getRadiusAtV(v);
        return Math.sqrt((Constants.G * Constants.getMassCentralBody()) * ((2 / r) - (1 / getSemiMajorAxis())));
    }

    public double getVelocity() {
        return getVelocityAtV(v);
    }

    public double getXPosAtV(double v) {
        return Constants.polarToCartesian(getRadiusAtV(v), Math.toRadians(v + w))[0];
    }

    public double getXPos() {
        return getXPosAtV(v);
    }

    public double getYPosAtV(double v) {
        return Constants.polarToCartesian(getRadiusAtV(v), Math.toRadians(v + w))[1];
    }

    public double getYPos() {
        return getYPosAtV(v);
    }

    public double getDelta(double time) {
        double a = getSemiMajorAxis();
        double b = getSemiMinorAxis();
        double e = getEccentricity();
        double r = radiusFromFoci(a, e, v);
        //double T = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / (Constants.G * Constants.M_EARTH));
        double n = 1 / Math.sqrt(Math.pow(a, 3) / (Constants.G * Constants.getMassCentralBody()));
        double delta = (a * b * n * time) / Math.pow(r, 2);
        return Math.toDegrees(delta);
    }

    public Vector2 getCartesianVelocityVectorAtV(double v) {
        Vector2 vec = new Vector2((float) getVelocityXAtV(v), (float) getVelocityYAtV(v));
        vec.rotateDeg((float)w);
        return vec;
    }

    public Vector2 getCartesianVelocityVector() {
        return getCartesianVelocityVectorAtV(v);
    }

    public double getAltitudeAboveSeaLevel() {
        double a = getSemiMajorAxis();
        double e = getEccentricity();
        return radiusFromFoci(a, e, v) - Constants.getRadiusCentralBody();
    }

    public double getFlightPathAngle() {
        double e = getEccentricity();
        return Math.atan((e * Math.sin(Math.toRadians(v))) / (1 + e * Math.cos(Math.toRadians(v))));
    }

    public double getRadiusAtV(double v) {
        double a = getSemiMajorAxis();
        double e = getEccentricity();
        return radiusFromFoci(a, e, v);
    }

    public double getIntersectionRadiusAtA(double a) {
        return getRadiusAtV(a - w);
    }

    public double[] getIntersectionCoordsFromA(double a) {
        return Constants.getOrbitalPosition(apoapsis, periapsis, a - w, w);
    }

    public double[] getIntersectionCoordsFromV(double v) {
        return Constants.getOrbitalPosition(apoapsis, periapsis, v, w);
    }

    private double radiusFromFoci(double a, double e, double theta) {
        return (a * (1 - Math.pow(e, 2))) / (1 + e * Math.cos(Math.toRadians(theta)));
    }

    // get velocity x in the perifocal frame
    // https://physics.stackexchange.com/questions/669946/how-to-calculate-the-direction-of-the-velocity-vector-for-a-body-that-moving-is
    private double getVelocityXAtV(double v){
        double e = getEccentricity();
        return getVelocityAtV(v) * (0 - Math.sin(Math.toRadians(v))) / (Math.sqrt(1 + Math.pow(e, 2) + 2 * e * Math.cos(Math.toRadians(v))));
    }

    private double getVelocityX() {
        return getVelocityXAtV(v);
    }

    // get velocity y in the perifocal frame
    // https://physics.stackexchange.com/questions/669946/how-to-calculate-the-direction-of-the-velocity-vector-for-a-body-that-moving-is
    private double getVelocityYAtV(double v) {
        double e = getEccentricity();
        return getVelocityAtV(v) * (e + Math.cos(Math.toRadians(v))) / (Math.sqrt(1 + Math.pow(e, 2) + 2 * e * Math.cos(Math.toRadians(v))));
    }

    private double getVelocityY() {
        return getVelocityYAtV(v);
    }
}
