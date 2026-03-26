package com.gvi.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
	private static final Logger log = LoggerFactory.getLogger(Sound.class);
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
			log.debug("Preload failed for sound index {}", i, e);
		}
	}

	public void setFile(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e){
			log.warn("Failed to load sound at index {}", i, e);
		}
	}

	public void play() {
		if (clip != null) clip.start();
	}

	public void loop() {
		if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void loop(int i) {
		if (clip != null) clip.loop(i);
	}

	public void stop() {
		if (clip != null) clip.stop();
	}

	public boolean isRunning(){
		if (clip != null) return clip.isRunning();
		else return false;
	}
}
