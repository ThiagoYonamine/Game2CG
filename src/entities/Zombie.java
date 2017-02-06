package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Zombie extends Entity {
	private static float SPEED_INC = 2.0f;
	private float speed;

	public Zombie(TexturedModel model, Vector3f position, Vector3f rots, float scale, float speed, Vector3f size) {
		super(model, position, rots, scale, size);
		this.speed = speed;
	}

	public void move(Vector3f playerPosition, float roty) {
		recalculateSpeed();
		setRotY(180 + roty);
		moveTo(playerPosition);
	}

	private void moveTo(Vector3f pos) {
		float angle = (float) Math.atan2(pos.x - this.position.x, pos.z - this.position.z);
		float distance = speed * DisplayManager.getFrameTimeSeconds();

		float dx = distance * (float) Math.sin(angle);
		float dz = distance * (float) Math.cos(angle);
		increasePosition(dx, 0, dz);
	}

	private void recalculateSpeed() {
		if (Math.random() < 0.3) {
			speed += SPEED_INC;
		}

		if (Math.random() < 0.1) {
			speed -= SPEED_INC;
		}

		speed = Math.min(speed, 25);
		speed = Math.max(10, speed);
	}

}
