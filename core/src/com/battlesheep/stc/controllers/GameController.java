package com.battlesheep.stc.controllers;

import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbiting;
import com.battlesheep.stc.game.Scenario;

import java.util.*;

public class GameController {

    // game information
    private long stepCounter;
    private float timeStep; // number of seconds to pass for each step.
    private long lastUpdate; // time of last update
    private Scenario scenario;

    // game rules
    private double shipMinDistance; // minimum distance ships must stay apart from one another, meters

    // earth info
    private int earthTime; // time on earth in-game in seconds
    private int earthTimeStep; // how much time in-game passes with each game step

    // agents
    private ArrayList<Orbiting> orbiting;

    // camera
    private CameraController camera;

    private static GameController instance;

    private GameController() {
        stepCounter = 0;
        timeStep = 0.001f;
        lastUpdate = 0;

        shipMinDistance = 100e3; // 10km

        earthTime = 0;
        earthTimeStep = 1;

        orbiting = new ArrayList<Orbiting>();

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
        iterateEarthTime();
        for(Orbiting o : orbiting) {
            o.step(earthTimeStep);
        }
    }

    private void iterateEarthTime() {
        earthTime += earthTimeStep;
        if (earthTime > Constants.EARTH_DAY_LENGTH) {
            earthTime -= Constants.EARTH_DAY_LENGTH;
        }
    }

    public int getEarthTime() {
        return earthTime;
    }

    public void setEarthTime(int time) {
        this.earthTime = time;
    }

    public void setEarthTimeStep(int step) {
        earthTimeStep = step;
    }

    public ArrayList<Orbiting> getOrbiting() {
        return orbiting;
    }

    public void addOrbiting(Orbiting o) {
        this.orbiting.add(0, o);
    }

    public void removeOrbiting(Orbiting o) {
        this.orbiting.remove(o);
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
}
