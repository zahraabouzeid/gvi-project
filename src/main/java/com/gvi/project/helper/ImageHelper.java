package com.gvi.project.helper;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {


	public static Image getImage(String path) throws IOException {
		InputStream stream = ImageHelper.class.getResourceAsStream(path);

		if (stream == null) {
			throw new IOException("Image not found: " + path);
		}

		return new Image(stream);
	}

}
