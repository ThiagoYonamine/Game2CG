package entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {

	private CollisionBox cbox;
	private TexturedModel model;
	protected Vector3f position;
	protected float rotX, rotY, rotZ;
	private float scale;

	public Entity(TexturedModel model, Vector3f position, Vector3f rots, float scale, Vector3f size) {
		this.model = model;
		this.position = position;
		this.rotX = rots.x;
		this.rotY = rots.y;
		this.rotZ = rots.z;
		this.scale = scale;

		this.cbox = new CollisionBox(position, size, rots);
	}

	public boolean collides(Entity e) {
		return CollisionBox.collides(this.cbox, e.getCollisionBox());
	}

	public boolean collides() {
		return CollisionBox.collides(this.cbox);
	}

	public Entity collides(List<Entity> entities) {
		for (Entity e : entities) {
			if (collides(e))
				return e;
		}
		return null;
	}

	public CollisionBox getCollisionBox() {
		return cbox;
	}

	public void increasePosition(float dx, float dy, float dz, boolean force) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;

		if (!force) {
			if (collides()) {
				this.position.y -= dy;
				this.position.x -= dx + (float) Math.random() * 2;
				this.position.z -= dz + (float) Math.random() * 2;
			}
		}
	}

	public void increasePosition(float dx, float dy, float dz) {
		increasePosition(dx, dy, dz, false);
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

}