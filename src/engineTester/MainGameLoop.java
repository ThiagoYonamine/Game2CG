package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import entities.Bala;
import entities.CollisionBox;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Zombie;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRender;
import renderEngine.OBJLoader;
import terrains.Terrain;

public class MainGameLoop {

	private final static int MIN_TREE_HEIGHT = 1;
	private final static int MAX_TREE_HEIGHT = 4;
	private final static int SHOT_DEBOUNCE_DELAY = 300;

	private static long lastShotTime = 0L;

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap2"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		RawModel model = OBJLoader.loadObjModel("pine", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("pine")));
		staticModel.getTexture().setHasTransparency(true);
		final List<Entity> entities = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities.add(new Entity(staticModel,
					new Vector3f(random.nextFloat() * 800 - 400, 0, (random.nextFloat() * -600) - 30),
					new Vector3f(0, 0, 0), scale, new Vector3f(5, 10, 5)));
		}

		RawModel model2 = OBJLoader.loadObjModel("fern", loader);
		TexturedModel staticModel2 = new TexturedModel(model2, new ModelTexture(loader.loadTexture("fern")));
		staticModel2.getTexture().setHasTransparency(true);
		staticModel2.getTexture().setUseFakeLighting(true);

		List<Entity> entities2 = new ArrayList<Entity>();
		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities2.add(new Entity(staticModel2,
					new Vector3f(random.nextFloat() * 800 - 400, -1f, random.nextFloat() * -600), new Vector3f(0, 0, 0),
					scale / 10, new Vector3f()));
		}

		RawModel dragonOBJ = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragon = new TexturedModel(dragonOBJ, new ModelTexture(loader.loadTexture("verde")));
		ModelTexture dragon_texture = dragon.getTexture();
		// Reflexo
		dragon_texture.setShineDamper(10); // tipo do material
		dragon_texture.setReflectivity(50); // reflexo

		Entity entityDragon = new Entity(dragon, new Vector3f(0, 0, -40), new Vector3f(0, 0, 0), 1,
				new Vector3f(5, 5, 5));

		RawModel model_arma = OBJLoader.loadObjModel("arma", loader);
		TexturedModel tx_arma = new TexturedModel(model_arma, new ModelTexture(loader.loadTexture("arma")));
		
		ModelTexture texture_arma = tx_arma.getTexture();
		// Reflexo
		texture_arma.setShineDamper(50); // tipo do material
		texture_arma.setReflectivity(20); // reflexo

		Entity entityArma = new Entity(tx_arma, new Vector3f(110, 10, -50), new Vector3f(0, 180, 0), 0.5f,
				new Vector3f(0, 0, 0));
		entityArma.increaseRotation(180, 0, 0);

		Light light = new Light(new Vector3f(0, 50, -30), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

		MasterRender renderer = new MasterRender();
		TexturedModel player_model = tx_arma;

		Player player = new Player(player_model, new Vector3f(0, 5, 0), new Vector3f(0, 180, 0), 0.5f,
				new Vector3f(2, 2, 2));

		RawModel model_zombie = OBJLoader.loadObjModel("Slasher", loader);
		TexturedModel tx_zombie = new TexturedModel(model_zombie, new ModelTexture(loader.loadTexture("Slasher")));

		List<Entity> zombies = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			zombies.add(
					new Zombie(tx_zombie, new Vector3f(random.nextFloat() * 800 - 400, 5, random.nextFloat() * -600),
							new Vector3f(0, 0, 0), 5, random.nextFloat() * 20, new Vector3f(4f, 4f, 4f)));
		}

		RawModel model_bala = OBJLoader.loadObjModel("bullet2", loader);
		TexturedModel tx_bala = new TexturedModel(model_bala, new ModelTexture(loader.loadTexture("mud")));

		List<Entity> check_collision = new ArrayList<>();
		check_collision.add(player);
		check_collision.add(entityDragon);
		check_collision.addAll(entities);
		check_collision.addAll(zombies);

		CollisionBox.setEntities(check_collision);

		List<Bala> balas = new ArrayList<Bala>();

		// hide the mouse
		Mouse.setGrabbed(true);

		while (!Display.isCloseRequested()) {
			player.move();
			for (Entity zombie : zombies) {
				((Zombie) zombie).move(player.getPosition(), player.getRotY());
				renderer.processEntity(zombie);
			}

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(entityDragon);

			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}

			for (Entity entity2 : entities2) {
				renderer.processEntity(entity2);
			}

			entityArma
					.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z));
			entityArma.setRotY(-player.getRotY());
			renderer.processEntity(entityArma);

			long thisTime = System.nanoTime();
			if (Mouse.isButtonDown(0) && (thisTime - lastShotTime >= 1E6 * SHOT_DEBOUNCE_DELAY)) {
				lastShotTime = thisTime;
				Bala b = new Bala(tx_bala,
						new Vector3f(player.getPosition().x, player.getPosition().y + 3, player.getPosition().z),
						new Vector3f(0, player.getRotY(), 0), 0.1f, new Vector3f(1, 1, 1));

				b.atira(player.getRotY());
				balas.add(b);
			}

			balas.removeIf(new Predicate<Bala>() {
				@Override
				public boolean test(Bala x) {
					// remove balas que estão longe ou que colidem com árvores
					return x.getTempo() > 100 || x.collides(entities) != null;
				}
			});

			List<Entity> to_remove = new ArrayList<>();
			for (Bala tiro : balas) {
				Entity z = tiro.collides(zombies);
				if (z != null) {
					to_remove.add(tiro);
					to_remove.add(z);
				}
			}
			balas.removeAll(to_remove);
			zombies.removeAll(to_remove);

			for (Bala tiro : balas) {
				renderer.processEntity(tiro);
				tiro.move();
			}

			renderer.render(light, player.getCamera());
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}