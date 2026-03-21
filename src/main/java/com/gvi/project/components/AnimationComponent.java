package com.gvi.project.components;

import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

import java.util.ArrayList;
import java.util.List;

public class AnimationComponent extends Component {

	public List<Sprite> sprites;
	private List<Integer> cycleOrder = new ArrayList<>();

	public double cycleLength = 1.0;          // duration of one cycle in seconds
	public double delay = 0.0;                // initial delay before starting
	public double delayBetweenCycles = 0.0;   // pause between looping cycles
	public boolean stopPlayerMovement = false;
	private boolean looping = false;

	private double timer = 0;
	private boolean playing = false;
	private boolean finished = false;
	private boolean started = false;
	private boolean inCooldown = false;

	public int currentFrame = 0;

	public Runnable onFinished;               // optional callback
	public Runnable onStarted;               // optional callback

	public AnimationComponent(String sheetPath, String spriteGroupId) {
		super("Animation");
		SpriteSheet sheet = new SpriteSheet(sheetPath);
		sprites = sheet.getGroupSpritesAsList(spriteGroupId);
		setCycleOrder();
	}

	// Set a start offset (e.g., for staggered animations)
	public void setStartOffset(double offset) {
		timer = offset;
		started = true;
	}

	public void isLooping(){
		looping = true;
		playing = true;
	}

	public void trigger() {
		playing = true;
		timer = 0;
		started = false;
		inCooldown = false;
		finished = false;
	}

	public void triggerLoop() {
		playing = true;
		timer = 0;
		started = false;
		inCooldown = false;
		finished = false;
		looping = true;
	}

	public void update(double delta) {
		if (!playing) return;

		timer += delta;

		// Initial delay
		if (!started) {
			if (timer >= delay) {
				timer -= delay;
				started = true;
			} else {
				return;
			}
		}

		// Cooldown
		if (inCooldown) {
			if (timer >= delayBetweenCycles) {
				timer = 0;
				inCooldown = false;
			} else {
				return;
			}
		}

		double frameDuration = cycleLength / cycleOrder.size();
		int index = (int)(timer / frameDuration);

		if (!looping) {

			if (index >= cycleOrder.size()) {
				index = cycleOrder.size() - 1;
				playing = false;
				finished = true;
				if (onFinished != null) {
					onFinished.run();
					onFinished = null;
				}
			}
		} else {
			if (timer >= cycleLength) {
				timer -= cycleLength;
				inCooldown = delayBetweenCycles > 0;
			}

			index = (int)(timer / frameDuration);
			index = Math.min(index, cycleOrder.size() - 1);
		}

		currentFrame = cycleOrder.get(index);
	}

	public Sprite getCurrentSprite() {
		return sprites.get(currentFrame);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setCycleOrder(){
		List<Integer> cycleOrder = new ArrayList<>();
		for (int i = 0; i < sprites.size(); i++) {
			cycleOrder.add(i);
		}
		this.cycleOrder = cycleOrder;
	}

    public void setCycleOrder(List<Integer> cycleOrder){
		this.cycleOrder = cycleOrder;
	}
}