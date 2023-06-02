package com.battlesheep.stc.game;

import com.battlesheep.stc.controllers.GameController;

import java.util.ArrayList;

public class Ship extends Orbit {

    private boolean isEncroached;
    private long lastChecked;

    public Ship(double apoapsis, double periapsis, double v, double w) {
        super(apoapsis, periapsis, v, w);
        isEncroached = false;
    }

    public ArrayList<Ship> checkIfEncroached(ArrayList<Orbit> orbit, int index) {
        GameController game = GameController.getInstance();
        ArrayList<Ship> encroachedShips = new ArrayList<Ship>();
        for (int i = index; i < orbit.size(); i++) {
            Orbit o = orbit.get(i);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                if (s.getAltitudeAboveSeaLevel() > getAltitudeAboveSeaLevel() + game.getShipMinDistance()) {
                    break;
                }
                if (Constants.distanceBetween(this, o) <= game.getShipMinDistance() && this != o){
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
}
