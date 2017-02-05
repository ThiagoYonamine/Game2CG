package entities;

import models.TexturedModel;
import renderEngine.DisplayManager;

import org.lwjgl.util.vector.Vector3f;

public class Zombie extends Entity {
	private static float SPEED_INC = 0.2f;
	private float speed;

	public Zombie(TexturedModel model, Vector3f position, Vector3f rots, float scale, float speed, Vector3f size) {
		super(model, position, rots, scale, size);
		this.speed = speed;
	}

	public void move(Vector3f playerPosition, float roty) {
		recalculateSpeed();
		setRotY(180 + roty);
		if (playerPosition.getX() > position.getX())
			increasePosition(speed * DisplayManager.getFrameTimeSeconds(), 0, 0);
		else
			increasePosition(-speed * DisplayManager.getFrameTimeSeconds(), 0, 0);

		if (playerPosition.getZ() > position.getZ())
			increasePosition(0, 0, speed * DisplayManager.getFrameTimeSeconds());
		else
			increasePosition(0, 0, -speed * DisplayManager.getFrameTimeSeconds());

	}

	private void recalculateSpeed() {
		if (Math.random() < 0.3) {
			speed += SPEED_INC;
		}

		if (Math.random() < 0.3) {
			speed -= SPEED_INC;
		}
		
		speed = Math.max(speed, 30);
		speed = Math.min(10, speed);
	}

}
