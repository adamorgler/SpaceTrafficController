package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.battlesheep.stc.game.Constants;
import com.battlesheep.stc.game.Orbiting;

public class InputController implements InputProcessor {

    private static InputController instance;

    private CameraController camera;
    private GameController game;
    private GUIController gui;

    private Vector2 lastTouch;
    private boolean selected;

    private InputController() {
        camera = CameraController.getInstance();
        game = GameController.getInstance();
        gui = GUIController.getInstance();

        lastTouch = new Vector2();
        selected = false;
    }

    public static InputController getInstance() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }

    @Override
    public boolean keyDown(int keycode) {
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
            for (Orbiting o : game.getOrbiting()) {
                double xPos = o.getXPos() / gui.getPixelScale();
                double yPos = o.getYPos() / gui.getPixelScale();
                //System.out.println("Ship pos: x: " + xPos + " y: " + yPos);
                double distance = Constants.distanceBetween(xPos, yPos, cursorPos.x, cursorPos.y);
                if (distance < game.getShipMinDistance() / gui.getPixelScale() && !selected) {
                    selected = true;
                    o.setSelected(selected);
                    camera.setFollowing(o);
                    // move selected orbiting object to top of list
                } else {
                    o.setSelected(false);
                }
            }
            selected = false;
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
