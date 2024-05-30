package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbit;

public class InputController implements InputProcessor {

    public static class KEY_BINDS {
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
            case KEY_BINDS.PAUSE: {
                game.paused = game.paused ? false : true;
                break;
            }
            case KEY_BINDS.DEV_MODE: {
                game.DEV_MODE = game.DEV_MODE ? false : true;
                break;
            }
            default:
                break;
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
        if (button == Input.Buttons.MIDDLE) {
            lastTouch.set(screenX, screenY);
        }
        if (button == Input.Buttons.LEFT) {
            Vector3 cursorPos = new Vector3(screenX, screenY, 0);
            cursorPos = camera.unproject(cursorPos);
            //System.out.println(" Cursor pos: x: " + cursorPos.x + " y: " + cursorPos.y);
            for (Orbit o : game.getOrbiting()) {
                o.setSelected(false);
            }
            camera.setFollowing(null);
            for (Orbit o : game.getOrbiting()) {
                double xPos = o.getXPos() / gui.getPixelScale();
                double yPos = o.getYPos() / gui.getPixelScale();
                //System.out.println("Ship pos: x: " + xPos + " y: " + yPos);
                double distance = Constants.distanceBetween(xPos, yPos, cursorPos.x, cursorPos.y);
                if (distance < game.getShipMinDistance() / gui.getPixelScale() / 2) {
                    o.setSelected(true);
                    camera.setFollowing(o);
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
