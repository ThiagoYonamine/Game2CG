package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Player extends Entity {

	private static final float WALK_SPEED = 20f;
	private static final float TURN_SPEED = 10;
	private static final float RUN_SPEED_FACTOR = 2f;
	private static final long IMMUNE_TIME = 2;

	private float currentForwardSpeed = 0;
	private float currentSidewardSpeed = 0;
	private float currentTurnSpeed = 0;

	private long last_hit = 0;
	private Camera camera;
	private int lifes;

	public Player(TexturedModel model, Vector3f position, Vector3f rots, float scale, Vector3f size) {
		super(model, position, rots, scale, size);

		reset();
		camera = new Camera(this);
	}

	public boolean is_immune_or_hit(long curr_time) {
		if (curr_time - last_hit > IMMUNE_TIME * 1E9) {
			last_hit = curr_time;
			lifes--;
			return true;
		} else {
			return false;
		}
	}

	public int move() {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);

		boolean running = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if (running) {
			this.currentForwardSpeed *= RUN_SPEED_FACTOR;
			this.currentSidewardSpeed *= RUN_SPEED_FACTOR;
		}

		float distanceForward = currentForwardSpeed * DisplayManager.getFrameTimeSeconds();
		float distanceSideward = currentSidewardSpeed * DisplayManager.getFrameTimeSeconds();

		walkForward(distanceForward);
		walkSideward(distanceSideward);
		this.camera.move();
		Mouse.setCursorPosition(0, 0);

		if (distanceForward != 0 || distanceSideward != 0) {
			return running ? 2 : 1;
		} else {
			return 0;
		}

	}

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentForwardSpeed = WALK_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentForwardSpeed = -WALK_SPEED;
		} else {
			this.currentForwardSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentSidewardSpeed = WALK_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentSidewardSpeed = -WALK_SPEED;
		} else {
			this.currentSidewardSpeed = 0;
		}

		float angleChange = Mouse.getDX() * TURN_SPEED;
		currentTurnSpeed = -angleChange;
	}

	public void walkForward(float distance) {
		float dx = distance * (float) Math.sin(Math.toRadians(rotY));
		float dz = distance * (float) Math.cos(Math.toRadians(rotY));
		increasePosition(dx, 0, dz);
	}

	public void walkSideward(float distance) {
		float dx = distance * (float) Math.sin(Math.toRadians(rotY - 90));
		float dz = distance * (float) Math.cos(Math.toRadians(rotY - 90));
		increasePosition(dx, 0, dz);
	}

	public Camera getCamera() {
		return this.camera;
	}

	public void reset() {
		lifes = Player.maxLifes();
	}

	public int lifes() {
		return lifes;
	}

	public static int maxLifes() {
		return 3;
	}

}
