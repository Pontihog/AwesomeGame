package com.me.rubisco;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;


public class GameAudio {
	public static Music song = Gdx.audio.newMusic(Gdx.files.internal("data/gangnam.mp3"));
	
	
	
	short[] samples = new short[44100*10];
	
	public void playMusic(boolean looping){
		song.setLooping(true);
		song.play();
	}
	
	
	public static void stopMusic(){
		song.stop();
	}
}
