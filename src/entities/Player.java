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

	private float currentForwardSpeed = 0;
	private float currentSidewardSpeed = 0;
	private float currentTurnSpeed = 0;
	

	private Camera camera;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		
		camera = new Camera(this);
	}

	public void move() {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distanceForward = currentForwardSpeed * DisplayManager.getFrameTimeSeconds();
		float distanceSideward = currentSidewardSpeed * DisplayManager.getFrameTimeSeconds();
		
		walkForward(distanceForward);
		walkSideward(distanceSideward);
		this.camera.move();
		Mouse.setCursorPosition(0, 0);
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

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			this.currentForwardSpeed *= RUN_SPEED_FACTOR;
			this.currentSidewardSpeed *= RUN_SPEED_FACTOR;
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

}
