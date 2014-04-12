package com.me.rubisco;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class GameAudio {
	public static Music song = Gdx.audio.newMusic(Gdx.files.internal("data/gangnam.mp3"));
	
	public static Sound reload = Gdx.audio.newSound(Gdx.files.internal("audio/gunz.mp3"));
	public static Sound shot = Gdx.audio.newSound(Gdx.files.internal("audio/shot.mp3"));
	public static Sound laser = Gdx.audio.newSound(Gdx.files.internal("audio/pewpew.wav"));
	public static Sound scatter = Gdx.audio.newSound(Gdx.files.internal("audio/scatter.wav"));
	public static Sound hit = Gdx.audio.newSound(Gdx.files.internal("audio/pawnch.mp3"));
	public static Sound breakit = Gdx.audio.newSound(Gdx.files.internal("audio/break.wav"));

	
	
	public void reload(){
		reload.play();
	}
	
	public void shot(){
		shot.play();
	}
	
	public void laser(){
		laser.setVolume(laser.play(), .3f);
	}
	
	public void scatter(){
		scatter.play();
	}
	
	public void hit(){
		hit.play();
	}
	
	public void breakit(){
		breakit.setVolume(breakit.play(), .6f);
	}

}
