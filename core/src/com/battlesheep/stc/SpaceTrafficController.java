package com.battlesheep.stc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.battlesheep.stc.controllers.CameraController;
import com.battlesheep.stc.controllers.GUIController;
import com.battlesheep.stc.controllers.GameController;
import com.battlesheep.stc.controllers.InputController;
import com.battlesheep.stc.game.Ship;

import java.util.Random;

public class SpaceTrafficController extends ApplicationAdapter {

	// controllers
	GUIController gui;
	GameController game;
	InputController input;
	CameraController camera;

	@Override
	public void create () {
		gui = GUIController.getInstance();
		game = GameController.getInstance();
		input = InputController.getInstance();
		Gdx.input.setInputProcessor(input);
		camera = CameraController.getInstance();

		test1();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		gui.renderFrame();
		game.step();
		camera.updatePosition();
	}
	
	@Override
	public void dispose () {

	}

	private void test1() {
//		Orbiting o1 = new Orbiting(400000, 400000, 0, 180);
//		Orbiting o2 = new Orbiting(4000000, 200000, 66, 90);
//		gameController.addOrbiting(o1);
//		gameController.addOrbiting(o2);
		Random rand = new Random();
		int upper = 15000000;
		int lower = 100000;
		int n = 500;
		for(int i = 0; i < n; i++) {
			int ap = rand.nextInt(upper - lower) + lower;
			int pe = rand.nextInt(upper - lower) + lower;
			int v = rand.nextInt(359);
			int w = rand.nextInt(359);
			Ship s = new Ship(ap, pe, v, w);
			s.createManeuver(v);
			game.addOrbiting(s);
		}
	}
}
