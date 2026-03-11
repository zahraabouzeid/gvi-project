package com.gvi.project.models.core;

import com.gvi.project.GamePanel;

public interface Renderable {
	int getY();
	void render(GamePanel gp);
}
