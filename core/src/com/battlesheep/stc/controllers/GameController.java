package com.battlesheep.stc.controllers;

import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbit;
import com.battlesheep.stc.game.Scenario;
import com.battlesheep.stc.game.Ship;

import java.util.*;

public class GameController {

    // dev stuff
    public boolean DEV_MODE = false;

    // enums
    public enum CentralBody {EARTH};

    // game information
    private long stepCounter;
    private float timeStep; // number of irl seconds to pass for each step.
    private long lastUpdate; // time of last update
    private Scenario scenario;

    // game rules
    private double shipMinDistance; // minimum distance ships must stay apart from one another, meters

    // planet info
    private int centralBodyTime; // time on earth in-game in seconds
    private int centralBodyTimeStep; // how much time in-game passes with each game step
    private CentralBody centralBody;

    // agents
    private ArrayList<Orbit> orbits;
    private ArrayList<Ship> encroachedShips;

    // camera
    private CameraController camera;

    private static GameController instance;

    private GameController() {
        stepCounter = 0;
        timeStep = 0.05f;
        lastUpdate = 0;

        shipMinDistance = 300e3; // 300km

        centralBodyTime = 0;
        centralBodyTimeStep = 1;
        centralBody = CentralBody.EARTH;

        orbits = new ArrayList<>();
        encroachedShips = new ArrayList<>();

        camera = CameraController.getInstance();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void step() {
        // check if the timeStep has passed before making another update
        Date date = new Date();
        long time = date.getTime();
        if (time < lastUpdate + (long)(timeStep * 1000)) {
            return;
        }
        lastUpdate = time;

        // step logic
        stepCounter++;
        iterateCentralBodyTime();
        for (Orbit o : orbits) {
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                s.resetClosestShipDistance();
            }
        }
        for (Ship s : encroachedShips) {
            s.setEncroached(false);
        }
        encroachedShips.clear();
        sortOrbitsByAltitude();
        for(int i = 0; i < orbits.size(); i++) {
            Orbit o = orbits.get(i);
            o.step(centralBodyTimeStep);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                encroachedShips.addAll(s.checkIfEncroached(orbits, i));
            }
        }
        camera.updatePosition();
    }

    private void sortOrbitsByAltitude() {
        orbits.sort(new OrbitAltitudeComparator());
    }

    private void iterateCentralBodyTime() {
        centralBodyTime += centralBodyTimeStep;
        if (centralBodyTime > Constants.getDayLengthCentralBody()) {
            centralBodyTime -= Constants.getDayLengthCentralBody();
        }
    }

    public int getCentralBodyTime() {
        return centralBodyTime;
    }

    public void setCentralBodyTime(int time) {
        this.centralBodyTime = time;
    }

    public void setCentralBodyTimeStep(int step) {
        centralBodyTimeStep = step;
    }

    public ArrayList<Orbit> getOrbiting() {
        return orbits;
    }

    public void addOrbiting(Orbit o) {
        this.orbits.add(0, o);
    }

    public void removeOrbiting(Orbit o) {
        this.orbits.remove(o);
    }

    public double getShipMinDistance() {
        return shipMinDistance;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario s) {
        this.scenario = s;
    }

    public CentralBody getCentralBody() {
        return centralBody;
    }

    private class OrbitAltitudeComparator implements Comparator<Orbit> {

        @Override
        public int compare(Orbit o1, Orbit o2) {
            double alt1 = o1.getAltitudeAboveSeaLevel();
            double alt2 = o2.getAltitudeAboveSeaLevel();
            if (alt1 < alt2) {
                return -1;
            } else if (alt1 > alt2) {
                return 1;
            }
            return 0;
        }
    }
}
