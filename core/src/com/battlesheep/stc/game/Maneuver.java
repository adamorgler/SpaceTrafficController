package com.battlesheep.stc.game;

import com.badlogic.gdx.math.Vector2;

public class Maneuver {

    private Orbit parentOrbit;
    private Orbit newOrbit;
    private double deltaVTangential;
    private double deltaVPerpendicular;
    private double v;

    public Maneuver(Orbit parentOrbit, double v){
        this.parentOrbit = parentOrbit;
        this.v = v;
        this.deltaVTangential = 0.0;
        this.deltaVPerpendicular = 0.0;
        calculateNewOrbit();
    }

    public double getV() {
        return v;
    }

    public Orbit getParentOrbit() {
        return parentOrbit;
    }

    public Orbit getNewOrbit() {
        return newOrbit;
    }

    public void addDeltaVTangential(double deltaV) {
        deltaVTangential += deltaV;
    }

    public void addDeltaVPerpendicular(double deltaV) {
        deltaVPerpendicular += deltaV;
    }

    public void changeV(double v) {
        this.v = v;
    }

    public void calculateNewOrbit() {
        double mu = Constants.getMassCentralBody() * Constants.G;
        double r = parentOrbit.getRadiusAtV(v);
        double velocityTangential = parentOrbit.getVelocityAtV(v) + deltaVTangential;
        double velocityPerpendicular = deltaVPerpendicular;
        double velocity = Math.sqrt(Math.pow(velocityPerpendicular, 2) + Math.pow(velocityTangential, 2));

    }

}
