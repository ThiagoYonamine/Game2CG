package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Zombie extends Entity{
	private float speed = 0.2f;

	public Zombie(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);

	}
	public void move(Vector3f playerPosition, float roty) {
		setRotY(180+roty);
		if(playerPosition.getX() > position.getX())
			increasePosition(speed, 0, 0);
		else 
			increasePosition(-speed, 0, 0);
		
		
		if(playerPosition.getZ() > position.getZ())
			increasePosition(0, 0, speed);
		else 
			increasePosition(0, 0, -speed);
	}

}
