package com.battlesheep.stc.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.battlesheep.stc.game.Orbit;

public class CameraController extends OrthographicCamera {

    private float xPos;
    private float yPos;
    private float zPos;
    private float xBound;
    private float yBound;
    private float zoomUpperBound;
    private float zoomLowerBound;

    private static CameraController instance;

    private Orbit following;
    private float xFollowingOffset;
    private float yFollowingOffset;

    private CameraController() {
        this.xPos = 0f;
        this.yPos = 0f;
        this.zPos = 0f;
        this.zoom = 2f;
        this.zoomLowerBound = 0.2f;
        this.zoomUpperBound = 4f;
        this.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.position.set(xPos, yPos, zPos);

        this.following = null;
        this.xFollowingOffset = 0f;
        this.yFollowingOffset = 0f;
    }

    public static CameraController getInstance() {
    if (instance == null) {
        instance = new CameraController();
    }
    return instance;
    }

    public void updatePosition() {
        GUIController gui = GUIController.getInstance();
        if (following != null) {
            setxPos((float) (following.getXPos() / gui.getPixelScale()));
            setyPos((float) (following.getYPos() / gui.getPixelScale()));
        }
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        if (xPos < -xBound) {
            xPos = -xBound;
        } else if (xPos > xBound) {
            xPos = xBound;
        }
        this.xPos = xPos;
        this.position.set(xPos, yPos, zPos);
    }

    public void moveXPos(float a) {
        setxPos(getxPos() - a);
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        if (yPos < -yBound) {
            yPos = -yBound;
        } else if (yPos > yBound) {
            yPos = yBound;
        }
        this.yPos = yPos;
        this.position.set(xPos, yPos, zPos);
    }

    public void moveYPos(float a) {
        setyPos(getyPos() + a);
    }


    public void setzPos(float zPos) {
        //nothing
    }

    public float getzPos() {
        return zPos;
    }

    public float getZoom() {
        return zoom;
    }

    public float getZoomInverted() {
        return (zoomUpperBound - zoom) + zoomLowerBound;
    }

    public void setZoom(float zoom) {
        if (zoom <= zoomUpperBound && zoom >= zoomLowerBound) {
            this.zoom = zoom;
        }
    }

    public void moveZoom(float a) {
        setZoom(getZoom() + a);
    }
    public float getZoomUpperBound() {
        return zoomUpperBound;
    }
    public float getZoomLowerBound() {
        return zoomLowerBound;
    }
    public void setxBound() {
        this.xBound = viewportWidth;
    }
    public void setxBound(float xBound) {
        this.xBound = xBound;
    }
    public void setyBound() {
        this.yBound = viewportHeight;
    }
    public void setyBound(float yBound) {
        this.yBound = yBound;
    }

    public void setFollowing(Orbit o) {
        following = o;
    }
    public boolean isFollowing() { return following != null; }

}
