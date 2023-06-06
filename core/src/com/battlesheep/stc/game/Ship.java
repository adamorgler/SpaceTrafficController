package com.battlesheep.stc.game;

import com.battlesheep.stc.controllers.GUIController;
import com.battlesheep.stc.controllers.GameController;

import java.util.ArrayList;

public class Ship extends Orbit {

    private boolean isEncroached;
    private double closestShipDistance;
    private long lastChecked;
    private Maneuver maneuver;

    public Ship(double apoapsis, double periapsis, double v, double w) {
        super(apoapsis, periapsis, v, w);
        isEncroached = false;
        closestShipDistance = Double.MAX_VALUE;
        maneuver = null;
    }

    public ArrayList<Ship> checkIfEncroached(ArrayList<Orbit> orbit, int index) {
        GameController game = GameController.getInstance();
        GUIController gui = GUIController.getInstance();
        ArrayList<Ship> encroachedShips = new ArrayList<Ship>();
        for (int i = index; i < orbit.size(); i++) {
            Orbit o = orbit.get(i);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                double distanceBetween = Constants.distanceBetween(this, s);
                if (distanceBetween < closestShipDistance && this != o) {
                    closestShipDistance = distanceBetween;
                }
                if (distanceBetween < s.closestShipDistance && this != o) {
                    s.closestShipDistance = distanceBetween;
                }
                if (s.getAltitudeAboveSeaLevel() > getAltitudeAboveSeaLevel() + game.getShipMinDistance() * gui.getShipMinDistanceShown()) {
                    break;
                }
                if (distanceBetween <= game.getShipMinDistance() && this != o){
                    isEncroached = true;
                    s.isEncroached = true;
                    encroachedShips.add(s);
                }
            }
        }
        if (isEncroached) {
            encroachedShips.add(this);
        }
        return encroachedShips;
    }

    public boolean isEncroached() {
        return isEncroached;
    }

    public void setEncroached(boolean isEncroached) {
        this.isEncroached = isEncroached;
    }

    public void resetClosestShipDistance() {
        this.closestShipDistance = Double.MAX_VALUE;
    }

    public double getClosestShipDistance() {
        return closestShipDistance;
    }

    public void createManeuver(double v) {
        maneuver = new Maneuver(this, v);
    }

    public Maneuver getManeuver() {
        return maneuver;
    }

    public void deleteManeuver() {
        this.maneuver = null;
    }
}
