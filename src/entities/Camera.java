package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
 
public class Camera {

	private Vector3f position;
	private float pitch = 10;
	private float yaw;

	private Player player;

	public Camera(Player player) {
		this.player = player;
		this.position = player.getPosition();
	}

	public void move() {
		calculatePitch();
		calculateCameraPosition();

		this.yaw = 180 - player.getRotY();
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	private void calculateCameraPosition() {
		position = new Vector3f(player.getPosition());
		position.y += 5;
	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;

		// Limita inclinação: não permite olhas pros pés ou pro céu
		pitch = Math.min(pitch, 22);
		pitch = Math.max(pitch, -30);
	}

}