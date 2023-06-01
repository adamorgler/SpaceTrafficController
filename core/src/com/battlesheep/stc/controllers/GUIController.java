package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbiting;
import com.battlesheep.stc.game.Ship;

public class GUIController {

    private double degreeTest = 0;

    private static GUIController instance;

    private ShapeRenderer shapeRenderer;
    private CameraController camera;
    private GameController game;

    private int pixelScale; // number of meters in a rendered pixel
    private int cameraBound; // distance from earth surface in meters to bind camera
    private int numLatLines; // number of latitude lines to draw
    private int numLongLines; // number of longitude lines to draw
    private int circleSegments;
    private int shipSize; // size of ship's square on map (radius)
    private int labelSize; // size of ap and pe labels on map
    private int selectionSize; // size of selection box around ship

    private GUIController() {
        shapeRenderer = new ShapeRenderer();
        camera = CameraController.getInstance();
        game = GameController.getInstance();

        pixelScale = 10000;
        cameraBound = 400000;
        numLatLines = 6;
        numLongLines = 8; // must be even
        circleSegments = 128;
        shipSize = 3;
        labelSize = 5;
        selectionSize = (int) game.getShipMinDistance() / 2 / pixelScale + 5;
    }

    public static GUIController getInstance() {
        if (instance == null) {
            instance = new GUIController();
        }
        return instance;
    }

    public void renderFrame() {
        // clear screen at start
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA); // blends color's alpha correctly
        Gdx.gl.glLineWidth(1.5f);

        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.setxBound((float) ((2 * Constants.getRadiusCentralBody() + cameraBound) / pixelScale));
        camera.setyBound((float) ((2 * Constants.getRadiusCentralBody() + cameraBound) / pixelScale));
        //camera.updatePosition();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderCentralBody();
        renderOrbiters();
    }

    private void renderCentralBody() {
        switch(game.getCentralBody()) {
            case EARTH:
                renderEarth();
                break;
        }
    }

    private void renderEarth() {
        // EARTH
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Atmosphere
        shapeRenderer.setColor(Color.SKY);
        shapeRenderer.circle(0,0, (int)(Constants.getRadiusCentralBody() + Constants.getAtmosphereCentralBody()) / pixelScale, circleSegments);
        // land
        shapeRenderer.setColor(Color.OLIVE);
        shapeRenderer.circle(0,0,(int)Constants.getRadiusCentralBody() / pixelScale, circleSegments);
        // arctic
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(0,0, (int)(Constants.getRadiusCentralBody() / 3) / pixelScale, circleSegments);
        // sun shadow
        shapeRenderer.setColor(new Color(0,0,0,0.5f));
        shapeRenderer.arc(0,0, (int)(Constants.getRadiusCentralBody() + Constants.getAtmosphereCentralBody()) / pixelScale + 1, 90, 180, circleSegments / 2);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // latitude lines
        shapeRenderer.setColor(Color.BLACK);
        for(int i = 0; i < numLatLines; i++) {
            shapeRenderer.circle(0,0, (int)(Constants.getRadiusCentralBody() * (i + 1) / numLatLines) / pixelScale, circleSegments);
        }
        // longitude lines
        for(int i = 0; i < numLongLines; i++) {
            if (i == 0) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.BLACK);
            }
            double r = Constants.getRadiusCentralBody() / pixelScale;
            double d = (360f * i / numLongLines) + (360f * game.getCentralBodyTime() / Constants.getDayLengthCentralBody());
            double a = Math.toRadians(d);
            shapeRenderer.line((float) Constants.polarToCartesian(r, a)[0],
                    (float) Constants.polarToCartesian(r, a)[1],
                    0,
                    0);
        }
        shapeRenderer.end();
    }

    private void renderOrbiters() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        // ISS test
//        shapeRenderer.setColor(Color.YELLOW);
//        shapeRenderer.circle(0,0, (int)(Constants.RADIUS_EARTH + 5000000) / pixelScale, circleSegments);
        // render orbits
        for(Orbiting o : game.getOrbiting()) {
            // https://farside.ph.utexas.edu/teaching/celestial/Celestial/node30.html
            drawOrbit(o, circleSegments);
            drawShip(o);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                drawMinDistance(s);
            }
            if (o.isSelected()) {
                drawApoapsisAndPeriapsis(o);
                drawVelocityVectors(o);
                drawSelectionBox(o);
            }
        }
        shapeRenderer.end();
        degreeTest += 1;
    }

    private double[] getOrbitalPosition(Orbiting o) {
        double ap = o.getApoapsis();
        double pe = o.getPeriapsis();
        double v = o.getV();
        double w = o.getW();
        return getOrbitalPosition(ap, pe, v, w);
    }

    private double[] getOrbitalPosition(double ap, double pe, double v, double w) {
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

    private void drawOrbit(Orbiting o, int segments) {
        if (!o.isSelected()) {
            return;
        }
        shapeRenderer.setColor(Color.WHITE);
        double ap = o.getApoapsis();
        double pe = o.getPeriapsis();
        double w = o.getW();
        for (int i = 0; i < segments; i++) {
            double theta1 = (360f * i / segments);
            double theta2 = (360f * (i + 1) / segments);
            double[] p1 = getOrbitalPosition(ap, pe, theta1, w);
            double[] p2 = getOrbitalPosition(ap, pe, theta2, w);;
            shapeRenderer.line((float) p1[0] / pixelScale, (float) p1[1] / pixelScale, (float) p2[0] / pixelScale, (float) p2[1] / pixelScale);
        }
    }

    private void drawShip(Orbiting o) {
        double[] p = getOrbitalPosition(o);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect((float) p[0] / pixelScale - shipSize, (float) p[1] / pixelScale - shipSize, shipSize * 2, shipSize * 2);
    }

    private void drawSelectionBox(Orbiting o) {
        double[] p = getOrbitalPosition(o);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect((float) p[0] / pixelScale - (float) selectionSize, (float) p[1] / pixelScale - (float) selectionSize, (float) selectionSize * 2, (float) selectionSize * 2);
    }

    private void drawMinDistance(Ship s) {
        double[] p = getOrbitalPosition(s);
        if (s.isEncroached()) {
            shapeRenderer.setColor(Color.RED);
        }
        else {
            shapeRenderer.setColor(Color.GREEN);
        }
        shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) game.getShipMinDistance() / 2 / pixelScale);
    }

    private void drawApoapsisAndPeriapsis(Orbiting o) {
        if (o.getPeriapsis() != o.getApoapsis()) {
            double w = o.getW();
            double ap = o.getApoapsis();
            double pe = o.getPeriapsis();
            double[] apPos = getOrbitalPosition(ap, pe, 180, w);
            double[] pePos = getOrbitalPosition(ap, pe, 0, w);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle((float) pePos[0] / pixelScale, (float) pePos[1] / pixelScale, labelSize * camera.zoom);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle((float) apPos[0] / pixelScale, (float) apPos[1] / pixelScale, labelSize * camera.zoom);
        }
    }

    private void drawVelocityVectors(Orbiting o) {
        Vector2 velocityVector = o.getVelocityVector();
        double velocityX = velocityVector.x;
        double velocityY = velocityVector.y;
        double xPos = o.getXPos();
        double yPos = o.getYPos();
        float vectorScale = 100;
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos + velocityX * vectorScale) / pixelScale, (float) (yPos) / pixelScale);
        shapeRenderer.setColor((Color.BLUE));
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos) / pixelScale, (float) (yPos + velocityY * vectorScale) / pixelScale);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos + velocityX * vectorScale) / pixelScale, (float) (yPos + velocityY * vectorScale) / pixelScale);
    }

    private double radiusFromFoci(double a, double e, double theta) {
        return (a * (1 - Math.pow(e, 2))) / (1 + e * Math.cos(Math.toRadians(theta)));
    }

    public double getPixelScale() {
        return pixelScale;
    }
}
