package com.me.rubisco;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;
import com.me.rubisco.screens.Play;


public class MyGdxGame extends Game {
	
	public static final String TITLE = "Awesome Game", VERSION = "Alpha 1.0", FPS = "";
	public static final String LOG = null;
	FPSLogger log;
	
	@Override
	public void create() {	
		log = new FPSLogger();
		setScreen(new Play());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {		
		super.render();
		//log.log();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
