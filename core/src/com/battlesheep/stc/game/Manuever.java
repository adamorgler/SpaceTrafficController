package com.battlesheep.stc.game;

import com.badlogic.gdx.math.Vector2;

public class Manuever {

    private Orbit parentOrbit;
    private Orbit newOrbit;
    private double deltaVTangential;
    private double deltaVPerpendicular;
    private double v;

    public Manuever(Orbit parentOrbit, int v){
        this.parentOrbit = parentOrbit;
        this.newOrbit = new Orbit(parentOrbit.getApoapsis(), parentOrbit.getPeriapsis(), parentOrbit.getV(), parentOrbit.getW());
        this.v = v;
        this.deltaVTangential = 0.0;
        this.deltaVPerpendicular = 0.0;
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
        Vector2 newVelocityVector = parentOrbit.getVelocityVector().add(getDeltaVVector());

    }

    private Vector2 getDeltaVVector() {
        return new Vector2((float) getDeltaVX(), (float) getDeltaVY());
    }

    private double getDeltaVX() {
        return 0.0;
    }

    private double getDeltaVY() {
        return 0.0;
    }


}
