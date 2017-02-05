package entities;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class CollisionBox {
	private static List<Entity> entities;
	private Vector3f size;
	private Vector3f position;
	private Vector3f rots;

	public CollisionBox(Vector3f position, Vector3f size, Vector3f rots) {
		this.position = position;
		this.size = size;
		this.rots = rots;
	}

	@Override
	public String toString() {
		String out = "";
		out += position.x + " " + position.y + " " + position.z + "\t";
		out += size.x + " " + size.y + " " + size.z + "\t";
		return out;
	}

	public static boolean collides(CollisionBox c1, CollisionBox c2) {
		if (c1 == c2)
			return false;

		Vector2f c1X = c1.X();
		Vector2f c1Y = c1.Y();
		Vector2f c1Z = c1.Z();

		Vector2f c2X = c2.X();
		Vector2f c2Y = c2.Y();
		Vector2f c2Z = c2.Z();

		return (c1X.x <= c2X.y && c1X.y >= c2X.x) && (c1Y.x <= c2Y.y && c1Y.y >= c2Y.x)
				&& (c1Z.x <= c2Z.y && c1Z.y >= c2Z.x);
	}

	private Vector2f X() {
		return new Vector2f(position.x, position.x + size.x);
	}

	private Vector2f Y() {
		return new Vector2f(position.y, position.y + size.y);
	}

	private Vector2f Z() {
		return new Vector2f(position.z, position.z + size.z);
	}

	public static void setEntities(List<Entity> check_collision) {
		entities = check_collision;
	}

	public static boolean collides(CollisionBox cbox) {
		for (Entity e : entities) {
			if (collides(cbox, e.getCollisionBox()))
				return true;
		}
		return false;
	}

}
