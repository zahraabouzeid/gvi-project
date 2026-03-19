package com.gvi.project;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
	Clip clip;
	URL[] soundURL = new URL[30];

	public Sound() {
		soundURL[0] = getClass().getResource("/sounds/Dungeon.wav");
		soundURL[1] = getClass().getResource("/sounds/coin.wav");
		soundURL[2] = getClass().getResource("/sounds/powerup.wav");
		soundURL[3] = getClass().getResource("/sounds/unlock.wav");
		soundURL[4] = getClass().getResource("/sounds/dooropen.wav");
		soundURL[5] = getClass().getResource("/sounds/receivedamage.wav");
		soundURL[6] = getClass().getResource("/sounds/gameover.wav");
	}

	public void preload(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			Clip c = AudioSystem.getClip();
			c.open(ais);
			c.close();
		} catch (Exception e) {
			// ignore preload errors
		}
	}

	public void setFile(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void play() {
		clip.start();
	}

	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void loop(int i) {
		clip.loop(i);
	}

	public void stop() {
		clip.stop();
	}
}
