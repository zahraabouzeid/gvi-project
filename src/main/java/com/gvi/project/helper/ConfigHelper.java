package com.gvi.project.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvi.project.models.core.Config;

import java.io.IOException;
import java.io.InputStream;

public class ConfigHelper {
	private static ObjectMapper mapper = new ObjectMapper();

	public static <T extends Config> T getConfig(Class<T> configClass, String filePath) {

		try (InputStream iStream = configClass.getResourceAsStream(filePath)) {

			if (iStream == null) {
				throw new RuntimeException("File not found in resources: %s%n".formatted(filePath));
			}

			return mapper.readValue(iStream, configClass);
		} catch (IOException e) {
			throw new RuntimeException("Error while parsing file: " + filePath, e);
		}
	}
}
