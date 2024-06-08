package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbit;
import com.battlesheep.stc.game.Ship;

public class InputController implements InputProcessor {

    public static class KEY_BINDS {
        public static final int INCREASE_TIME = Input.Keys.PERIOD;
        public static final int DECREASE_TIME = Input.Keys.COMMA;
        public static final int PAUSE = Input.Keys.SPACE;
        public static final int DEV_MODE = Input.Keys.F11;
    }

    private static InputController instance;

    private CameraController camera;
    private GameController game;
    private GUIController gui;

    private Vector2 lastTouch;

    private InputController() {
        camera = CameraController.getInstance();
        game = GameController.getInstance();
        gui = GUIController.getInstance();

        lastTouch = new Vector2();
    }

    public static InputController getInstance() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case KEY_BINDS.INCREASE_TIME: {
                game.increaseStep();
                break;
            }
            case KEY_BINDS.DECREASE_TIME: {
                game.decreaseStep();
                break;
            }
            case KEY_BINDS.PAUSE: {
                game.paused = game.paused ? false : true;
                break;
            }
            case KEY_BINDS.DEV_MODE: {
                game.DEV_MODE = game.DEV_MODE ? false : true;
                break;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 cursorPos = camera.getCursorPosition();

        if (button == Input.Buttons.MIDDLE) {
            lastTouch.set(screenX, screenY);
        }
        if (button == Input.Buttons.LEFT) {
            camera.setFollowing(null);
            if (gui.uiState == GUIController.UI_STATE.ORBIT_SELECTED) {
                Orbit selectedOrbit = gui.selectedOrbit;
                if (camera.cursorIntersectsOrbit(selectedOrbit) && selectedOrbit instanceof Ship) {
                    Ship selectedShip = (Ship)selectedOrbit;
                    double a = Math.toDegrees(camera.getCursorPositionPolar()[1]);
                    selectedShip.createManeuver(a - selectedShip.getW());
                    return true;
                } else {
                    gui.uiState = GUIController.UI_STATE.DEFAULT;
                    gui.selectedOrbit = null;
                    if(selectedOrbit instanceof Ship) {
                        ((Ship)(selectedOrbit)).deleteManeuver();
                    }
                }
            }
            for (Orbit o : game.getOrbiting()) {
                double xPos = o.getXPos() / GUIController.pixelScale;
                double yPos = o.getYPos() / GUIController.pixelScale;
                //System.out.println("Ship pos: x: " + xPos + " y: " + yPos);
                double distance = Constants.distanceBetween(xPos, yPos, cursorPos.x, cursorPos.y);
                if (distance < game.getShipMinDistance() / GUIController.pixelScale / 2) {
                    gui.selectedOrbit = o;
                    camera.setFollowing(o);
                    gui.uiState = GUIController.UI_STATE.ORBIT_SELECTED;
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            Vector2 newTouch = new Vector2(screenX, screenY);
            Vector2 delta = newTouch.cpy().sub(lastTouch);
            lastTouch = newTouch;
            camera.setFollowing(null);
            camera.moveXPos(delta.x * camera.zoom);
            camera.moveYPos(delta.y * camera.zoom);

        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            camera.moveZoom(0.2f);
        } else if (amountY < 0) {
            camera.moveZoom(-0.2f);
        } else {
            return false;
        }
        return true;
    }
}
