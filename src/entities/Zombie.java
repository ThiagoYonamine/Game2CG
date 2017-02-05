package entities;

import models.TexturedModel;
import renderEngine.DisplayManager;

import org.lwjgl.util.vector.Vector3f;

public class Zombie extends Entity {
	private float speed;

	public Zombie(TexturedModel model, Vector3f position, Vector3f rots, float scale, float speed, Vector3f size) {
		super(model, position, rots, scale, size);
		this.speed = speed;
	}

	public void move(Vector3f playerPosition, float roty) {
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

}
