package com.battlesheep.stc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.battlesheep.stc.controllers.GUIController;
import com.battlesheep.stc.controllers.GameController;
import com.battlesheep.stc.controllers.InputController;
import com.battlesheep.stc.game.Orbiting;

import java.util.Random;

public class SpaceTrafficController extends ApplicationAdapter {

	// controllers
	GUIController gui;
	GameController game;
	InputController input;

	@Override
	public void create () {
		gui = GUIController.getInstance();
		game = GameController.getInstance();
		input = InputController.getInstance();
		Gdx.input.setInputProcessor(input);

		tests();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		gui.renderFrame();
		game.step();
	}
	
	@Override
	public void dispose () {

	}

	private void tests() {
//		Orbiting o1 = new Orbiting(400000, 400000, 0, 180);
//		Orbiting o2 = new Orbiting(4000000, 200000, 66, 90);
//		gameController.addOrbiting(o1);
//		gameController.addOrbiting(o2);
		Random rand = new Random();
		int upper = 15000000;
		int lower = 100000;
		int n = 100;
		for(int i = 0; i < n; i++) {
			int ap = rand.nextInt(upper - lower) + lower;
			int pe = rand.nextInt(upper - lower) + lower;
			int v = rand.nextInt(359);
			int w = rand.nextInt(359);
			Orbiting o = new Orbiting(ap, pe, v, w);
			game.addOrbiting(o);
		}
	}
}
