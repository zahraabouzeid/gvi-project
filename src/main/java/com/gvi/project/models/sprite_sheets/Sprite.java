package com.gvi.project.models.sprite_sheets;

import javafx.scene.image.Image;

public class Sprite {
	public Image image;
	public int imageHeight;
	public int imageWidth;
	public boolean hasCollision = false;

	public Sprite(Image image, int imageHeight, int imageWidth, boolean hasCollision) {
		this.image = image;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.hasCollision = hasCollision;
	}

	public Sprite(Image image, int imageHeight, int imageWidth) {
		this.image = image;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
	}

	public Sprite() {}
}
