package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Bala extends Entity {
	private double direcao;
	private int tempo = 0;
	private static final int SHOT_SPEED = 500;

	public Bala(TexturedModel model, Vector3f position, Vector3f rots, float scale, Vector3f size) {
		super(model, position, rots, scale, size);

	}

	public void atira(float roty) {
		direcao = Math.toRadians(roty);
		// increaseRotation(0,0,90-(float) direcao);

	}

	public void move() {
		float dx = DisplayManager.getFrameTimeSeconds() * (float) Math.sin(direcao) * SHOT_SPEED;
		float dz = DisplayManager.getFrameTimeSeconds() * (float) Math.cos(direcao) * SHOT_SPEED;
		increasePosition(dx, 0, dz, true);
		tempo++;
	}

	public int getTempo() {
		return tempo;
	}
}
