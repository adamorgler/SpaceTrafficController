package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Maneuver;
import com.battlesheep.stc.game.Orbit;
import com.battlesheep.stc.game.Ship;

public class GUIController {

    public enum UI_STATE {
        DEFAULT,
        ORBIT_SELECTED,
    }

    public UI_STATE uiState;
    public Orbit selectedOrbit;

    private double degreeTest = 0;

    private static GUIController instance;

    private ShapeRenderer shapeRenderer;
    private CameraController camera;
    private GameController game;

    public static int pixelScale; // number of meters in a rendered pixel
    private int cameraBound; // distance from earth surface in meters to bind camera
    private int numLatLines; // number of latitude lines to draw
    private int numLongLines; // number of longitude lines to draw
    private int circleSegments;
    private int shipSize; // size of ship's square on map (radius)
    private int labelSize; // size of ap and pe labels on map
    private int maneuverSize;
    private int selectionSize; // size of selection box around ship
    private float shipMinDistanceShown; // multiplier of ship min distance in which it is shown on the map
    public static int manueverNodeToolShowDistance; // distance from oribit that manuever node placement tool is shown in pixles

    private GUIController() {
        uiState = UI_STATE.DEFAULT;

        shapeRenderer = new ShapeRenderer();
        camera = CameraController.getInstance();
        game = GameController.getInstance();

        pixelScale = 10000;
        cameraBound = 2000000;
        numLatLines = 6;
        numLongLines = 8; // must be even
        circleSegments = 128;
        shipSize = 3;
        labelSize = 5;
        maneuverSize = 8;
        selectionSize = shipSize;
        // selectionSize = (int) game.getShipMinDistance() / 2 / pixelScale + 5;
        shipMinDistanceShown = 2.0f;
        manueverNodeToolShowDistance = 20;
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

    public float getShipMinDistanceShown() {
        return shipMinDistanceShown;
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
            double d = (360f * i / numLongLines) + (360f * game.time / Constants.getDayLengthCentralBody());
            double a = Math.toRadians(d);
            shapeRenderer.line((float) Constants.polarToCartesian(r, a)[0],
                    (float) Constants.polarToCartesian(r, a)[1],
                    0,
                    0);
        }
        shapeRenderer.end();
    }

    private void renderOrbiters() {
        // render orbits
        for(Orbit o : game.getOrbiting()) {
            // https://farside.ph.utexas.edu/teaching/celestial/Celestial/node30.html
            drawShip(o);
            if (o instanceof Ship) {
                Ship s = (Ship) o;
                drawMinDistance(s);
            }
            if (isSelected(o)) {
                drawSelected(o);
            }
            if (isSelected(o) && o instanceof Ship) {
                Ship s = (Ship) o;
                drawOrbit(o, circleSegments);
                drawApoapsisAndPeriapsis(o);
                drawMouseIntersection(o);
                drawManeuver(s);
            }
            if (game.DEV_MODE && o instanceof Ship) {
                Ship s = (Ship) o;
                drawShipMinDistanceShownDistance(s);
            }
            if (game.DEV_MODE && isSelected(o)) {
                drawVelocityVectors(o);
            }
        }
        degreeTest += 1;
    }

    private void drawOrbit(Orbit o, int segments) {
        double ap = o.getApoapsis();
        double pe = o.getPeriapsis();
        double w = o.getW();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < segments; i++) {
            double theta1 = (360f * i / segments);
            double theta2 = (360f * (i + 1) / segments);
            double[] p1 = Constants.getOrbitalPosition(ap, pe, theta1, w);
            double[] p2 = Constants.getOrbitalPosition(ap, pe, theta2, w);;
            shapeRenderer.line((float) p1[0] / pixelScale, (float) p1[1] / pixelScale, (float) p2[0] / pixelScale, (float) p2[1] / pixelScale);
        }
        shapeRenderer.end();
    }

    private void drawShip(Orbit o) {
        double[] p = Constants.getOrbitalPosition(o);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect((float) p[0] / pixelScale - shipSize, (float) p[1] / pixelScale - shipSize, shipSize * 2, shipSize * 2);
        shapeRenderer.end();
    }

    private void drawSelected(Orbit o) {
        double[] p = Constants.getOrbitalPosition(o);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect((float) p[0] / pixelScale - (float) selectionSize, (float) p[1] / pixelScale - (float) selectionSize, (float) selectionSize * 2, (float) selectionSize * 2);
        shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) game.getShipMinDistance() / 2 / pixelScale);
        shapeRenderer.end();
    }

    private void drawMinDistance(Ship s) {
        if (s.getClosestShipDistance() > game.getShipMinDistance() * shipMinDistanceShown) {
            return;
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (s.isEncroached()) {
            shapeRenderer.setColor(Color.RED);
        }
        else {
            shapeRenderer.setColor(Color.GREEN);
        }
        double[] p = Constants.getOrbitalPosition(s);
        shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) game.getShipMinDistance() / 2 / pixelScale);
        shapeRenderer.end();
    }

    private void drawApoapsisAndPeriapsis(Orbit o) {
        if (o.getPeriapsis() != o.getApoapsis()) {
            double w = o.getW();
            double ap = o.getApoapsis();
            double pe = o.getPeriapsis();
            double[] apPos = Constants.getOrbitalPosition(ap, pe, 180, w);
            double[] pePos = Constants.getOrbitalPosition(ap, pe, 0, w);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle((float) pePos[0] / pixelScale, (float) pePos[1] / pixelScale, labelSize * camera.zoom);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle((float) apPos[0] / pixelScale, (float) apPos[1] / pixelScale, labelSize * camera.zoom);
            shapeRenderer.end();
        }
    }

    private void drawManeuver(Ship s) {
        Maneuver m = s.getManeuver();
        if (m == null) return;
        double[] p = getManeuverNodePosition(m);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) maneuverSize * camera.zoom);
        shapeRenderer.end();
    }

    private void drawMouseIntersection(Orbit o) {
        double[] cursorPosPolar = camera.getCursorPositionPolar();
        double a = Math.toDegrees(cursorPosPolar[1]);
        double[] p = o.getIntersectionCoordsFromA(a);
        if (camera.cursorIntersectsOrbit(o)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) maneuverSize * camera.zoom);
            shapeRenderer.end();
        }
    }

    private double[] getManeuverNodePosition(Maneuver m) {
        Orbit o = m.getParentOrbit();
        double v = m.getV();
        return o.getIntersectionCoordsFromV(v);
    }

    private boolean isSelected(Orbit o) {
        return this.selectedOrbit == o;
    }

    // DEV STUFF ==============================================================================================

    private void drawVelocityVectors(Orbit o) {
        Vector2 velocityVector = o.getCartesianVelocityVector();
        double velocityX = velocityVector.x;
        double velocityY = velocityVector.y;
        double xPos = o.getXPos();
        double yPos = o.getYPos();
        float vectorScale = 100;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos + velocityX * vectorScale) / pixelScale, (float) (yPos) / pixelScale);
        shapeRenderer.setColor((Color.BLUE));
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos) / pixelScale, (float) (yPos + velocityY * vectorScale) / pixelScale);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.line((float) xPos / pixelScale, (float) yPos / pixelScale, (float) (xPos + velocityX * vectorScale) / pixelScale, (float) (yPos + velocityY * vectorScale) / pixelScale);
        shapeRenderer.end();
    }

    private void drawShipMinDistanceShownDistance(Ship s) {
        double[] p = Constants.getOrbitalPosition(s);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle((float) p[0] / pixelScale, (float) p[1] / pixelScale, (float) game.getShipMinDistance() / 2 / pixelScale * shipMinDistanceShown);
        shapeRenderer.end();
    }
}
