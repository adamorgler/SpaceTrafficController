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
    private float secondsPerFrame; // number of irl seconds to pass for each step.
    private long lastUpdate; // time of last update
    private Scenario scenario;
    public boolean paused;

    // game rules
    private double shipMinDistance; // minimum distance ships must stay apart from one another, meters

    // planet info
    public float time; // time on earth in-game in seconds
    private int timeStepIndex; // how much time in-game passes with each game step
    private float[] timeSteps;
    private CentralBody centralBody;

    // agents
    private ArrayList<Orbit> orbits;
    private ArrayList<Ship> encroachedShips;

    // camera
    private CameraController camera;

    private static GameController instance;

    private GameController() {
        stepCounter = 0;
        secondsPerFrame = 1f / 30f;
        lastUpdate = 0;

        shipMinDistance = 300e3; // 300km

        time = 0f;
        timeStepIndex = 0; // how much simulated time passed for every real life second in seconds
        timeSteps = new float[] {10f, 20f, 40f, 80f, 160f, 320f, 640f, 1280f};
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
        if (paused) return;

        // check if the timeStep has passed before making another update
        Date date = new Date();
        long time = date.getTime();
        if (time < lastUpdate + (long)(secondsPerFrame * 1000)) {
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
            o.step(timeSteps[timeStepIndex] * secondsPerFrame);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                encroachedShips.addAll(s.checkIfEncroached(orbits, i));
            }
        }
    }

    public void increaseStep() {
        if (timeStepIndex < timeSteps.length - 1) timeStepIndex++;
    }

    public void decreaseStep() {
        if (timeStepIndex > 0) timeStepIndex--;
    }

    private void sortOrbitsByAltitude() {
        orbits.sort(new OrbitAltitudeComparator());
    }

    private void iterateCentralBodyTime() {
        time += timeStepIndex * secondsPerFrame;
        if (time > Constants.getDayLengthCentralBody()) {
            time -= Constants.getDayLengthCentralBody();
        }
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
