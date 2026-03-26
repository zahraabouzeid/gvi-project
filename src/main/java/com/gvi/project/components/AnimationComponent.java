package com.gvi.project.components;

import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

import java.util.ArrayList;
import java.util.List;

public class AnimationComponent extends Component {

	public List<Sprite> sprites;
	private List<Integer> cycleOrder = new ArrayList<>();

	public double cycleLength = 1.0;
	public double delay = 0.0;
	public double delayBetweenCycles = 0.0;

	public boolean pingPongAnimation = false;
	private boolean looping = false;

	private double timer = 0;
	private double startOffset = 0;
	private boolean playing = false;
	private boolean finished = false;
	private boolean started = false;
	private boolean inCooldown = false;

	private int direction = 1; // 1 = forward, -1 = backward

	public int currentFrame = 0;

	public Runnable onFinished;
	public Runnable onStarted;

	public AnimationComponent(String sheetPath, String spriteGroupId) {
		super("Animation");
		SpriteSheet sheet = new SpriteSheet(sheetPath);
		sprites = sheet.getGroupSpritesAsList(spriteGroupId);
		setCycleOrder();
	}

	public void setStartOffset(double offset) {
		startOffset = offset;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public void trigger() {
		playing = true;
		timer = startOffset;
		started = false;
		inCooldown = false;
		finished = false;
		direction = 1;

		if (onStarted != null) {
			onStarted.run();
		}
	}

	public void triggerLoop() {
		trigger();
		setLooping(true);
	}

	// 🚪 Für Tür etc.
	public void invertCycleOrder() {
		List<Integer> inverted = new ArrayList<>();
		for (int i = cycleOrder.size() - 1; i >= 0; i--) {
			inverted.add(cycleOrder.get(i));
		}
		cycleOrder = inverted;
	}

	public void playForward() {
		direction = 1;
		trigger();
	}

	public void playBackward() {
		invertCycleOrder();
		direction = -1;
		trigger();
	}

	public void update(double delta) {
		if (!playing) return;

		timer += delta;

		// Initial Delay
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

		// 🔁 PingPong Handling
		if (pingPongAnimation) {
			if (index >= cycleOrder.size()) {
				direction = -1;
				timer = cycleLength;
				index = cycleOrder.size() - 1;
			} else if (index < 0) {
				direction = 1;
				timer = 0;
				index = 0;

				if (!looping) {
					playing = false;
					finished = true;
					if (onFinished != null) onFinished.run();
				}
			}
		}
		// 🔁 Normal Looping
		else if (looping) {
			if (timer >= cycleLength) {
				timer -= cycleLength;
				inCooldown = delayBetweenCycles > 0;
			} else if (timer < 0) {
				timer += cycleLength;
			}

			index = Math.max(0, Math.min(index, cycleOrder.size() - 1));
		}
		// ▶️ One-shot
		else {
			if (index >= cycleOrder.size()) {
				index = cycleOrder.size() - 1;
				playing = false;
				finished = true;
				if (onFinished != null) onFinished.run();
			} else if (index < 0) {
				index = 0;
			}
		}

		currentFrame = cycleOrder.get(index);
	}

	public Sprite getCurrentSprite() {
		return sprites.get(currentFrame);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setCycleOrder() {
		List<Integer> order = new ArrayList<>();
		for (int i = 0; i < sprites.size(); i++) {
			order.add(i);
		}
		this.cycleOrder = order;
	}

	public void setCycleOrder(List<Integer> cycleOrder) {
		this.cycleOrder = cycleOrder;
	}
}