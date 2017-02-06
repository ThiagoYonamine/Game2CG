package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
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
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRender;
import renderEngine.OBJLoader;
import terrains.Terrain;

public class MainGameLoop {
	private enum State {
		PLAYING, WIN, LOSE
	};

	private final static int MIN_TREE_HEIGHT = 1;
	private final static int MAX_TREE_HEIGHT = 4;
	private final static int SHOT_DEBOUNCE_DELAY = 300;

	private static long lastShotTime = 0L;

	private State state = State.PLAYING;
	Player player;

	MasterRender renderer;
	Loader loader;
	GuiTexture ganhou;
	GuiRenderer guiRenderer;

	List<GuiTexture> guis = new ArrayList<>();
	final List<Entity> entities = new ArrayList<>();
	List<Entity> entities2 = new ArrayList<>();
	List<Entity> zombies = new ArrayList<>();
	List<Bala> balas = new ArrayList<>();
	List<GuiTexture> lifes = new ArrayList<>();

	private int zombie_count = 0;
	TexturedModel tx_bala, tx_zombie;
	Random random = new Random();

	Terrain terrain, terrain2;
	Entity entityArma;
	Light light;

	private void init() {
		DisplayManager.createDisplay();
		loader = new Loader();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap2"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		// GUIs
		lifes.add(new GuiTexture(loader.loadTexture("life"), new Vector2f(-0.8f, -0.8f), new Vector2f(0.1f, 0.175f)));
		lifes.add(new GuiTexture(loader.loadTexture("life"), new Vector2f(-0.7f, -0.8f), new Vector2f(0.1f, 0.175f)));
		lifes.add(new GuiTexture(loader.loadTexture("life"), new Vector2f(-0.6f, -0.8f), new Vector2f(0.1f, 0.175f)));
		guis.addAll(lifes);

		ganhou = new GuiTexture(loader.loadTexture("ganhou"), new Vector2f(0f, 0f), new Vector2f(1f, 1f));

		guiRenderer = new GuiRenderer(loader);

		//
		RawModel model = OBJLoader.loadObjModel("pine", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("pine")));
		staticModel.getTexture().setHasTransparency(true);

		for (int i = 0; i < 110; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities.add(new Entity(staticModel,
					new Vector3f(random.nextFloat() * -800, 0, (random.nextFloat() * -750) - 30), new Vector3f(0, 0, 0),
					scale, new Vector3f(5, 10, 5)));
		}

		for (int i = 0; i < 90; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities.add(
					new Entity(staticModel, new Vector3f(random.nextFloat() * 800, 0, (random.nextFloat() * -750) - 30),
							new Vector3f(0, 0, 0), scale, new Vector3f(5, 10, 5)));
		}

		RawModel model2 = OBJLoader.loadObjModel("fern", loader);
		TexturedModel staticModel2 = new TexturedModel(model2, new ModelTexture(loader.loadTexture("fern")));
		staticModel2.getTexture().setHasTransparency(true);
		staticModel2.getTexture().setUseFakeLighting(true);

		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities2.add(new Entity(staticModel2,
					new Vector3f(random.nextFloat() * 800 - 400, -1f, random.nextFloat() * -600), new Vector3f(0, 0, 0),
					scale / 10, new Vector3f()));
		}

		RawModel model_arma = OBJLoader.loadObjModel("arma", loader);
		TexturedModel tx_arma = new TexturedModel(model_arma, new ModelTexture(loader.loadTexture("arma")));

		ModelTexture texture_arma = tx_arma.getTexture();
		// Reflexo
		texture_arma.setShineDamper(10); // tipo do material
		texture_arma.setReflectivity(15); // reflexo

		entityArma = new Entity(tx_arma, new Vector3f(110, 10, -50), new Vector3f(180, 180, 0), 0.5f,
				new Vector3f(0, 0, 0));

		light = new Light(new Vector3f(0, 9000, -5000), new Vector3f(0.2f, 0.2f, 0.2f));

		terrain = new Terrain(0, -1, loader, texturePack, blendMap);
		terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

		renderer = new MasterRender();
		TexturedModel player_model = tx_arma;
		// usar -440, 5, -370
		player = new Player(player_model, new Vector3f(-440, 5, -370), new Vector3f(0, 0, 0), 0.5f,
				new Vector3f(4, 4, 4));

		RawModel model_zombie = OBJLoader.loadObjModel("Slasher", loader);
		tx_zombie = new TexturedModel(model_zombie, new ModelTexture(loader.loadTexture("Slasher")));

		for (int i = 0; i < 20; i++) {
			zombies.add(
					new Zombie(tx_zombie, new Vector3f(random.nextFloat() * 800 - 400, 5, random.nextFloat() * -600),
							new Vector3f(0, 0, 0), 5, random.nextFloat() * 20, new Vector3f(4f, 4f, 4f)));
		}

		RawModel model_bala = OBJLoader.loadObjModel("bullet2", loader);
		tx_bala = new TexturedModel(model_bala, new ModelTexture(loader.loadTexture("mud")));
		ModelTexture texture_bala = tx_bala.getTexture();
		// Reflexo
		texture_bala.setShineDamper(10); // tipo do material
		texture_bala.setReflectivity(200); // reflexo

		List<Entity> check_collision = new ArrayList<>();
		check_collision.add(player);
		check_collision.addAll(entities);
		check_collision.addAll(zombies);

		CollisionBox.setEntities(check_collision);

		// hide the mouse
		Mouse.setGrabbed(true);
	}

	private void playing() {
		player.move();

		for (Entity zombie : zombies) {
			if (CollisionBox.collides(player.getCollisionBox(), zombie.getCollisionBox())) {
				if (player.is_immune_or_hit(System.nanoTime())) {

					int x = player.lifes();
					guis.remove(lifes.get(x));
				}
			}

			((Zombie) zombie).move(player.getPosition(), player.getRotY());
			renderer.processEntity(zombie);
		}

		if (player.lifes() == 0) {
			state = State.LOSE;
			guis.add(ganhou);
			return;
		}

		// renderer.processEntity(player);
		renderer.processTerrain(terrain);
		renderer.processTerrain(terrain2);

		for (Entity entity : entities) {
			renderer.processEntity(entity);
		}

		for (Entity entity2 : entities2) {
			renderer.processEntity(entity2);
		}

		entityArma.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z));
		entityArma.setRotY(-player.getRotY());
		renderer.processEntity(entityArma);

		// TODO chamar funçao ganhou quando colidir com dragon?
		// trocar KEY_G por colidiu drag?
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			guis.add(ganhou);
			state = State.WIN;
			return;
		}

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

				zombie_count++;
			}
		}

		if (zombie_count > 4) {
			for (int i = 0; i < zombie_count + 1; ++i) {
				zombies.add(new Zombie(tx_zombie,
						new Vector3f(random.nextFloat() * 800 - 400, 5, random.nextFloat() * -600),
						new Vector3f(0, 0, 0), 5, random.nextFloat() * 20, new Vector3f(4f, 4f, 4f)));
			}
			zombie_count = 0;
		}

		balas.removeAll(to_remove);
		zombies.removeAll(to_remove);

		for (Bala tiro : balas) {
			renderer.processEntity(tiro);
			tiro.move();
		}

		renderer.render(light, player.getCamera());
		guiRenderer.render(guis);
	}

	int cont = 0;

	private void reset() {
		player.reset();
		state = State.PLAYING;
	}

	private void just_render() {
		for (Entity zombie : zombies) {
			renderer.processEntity(zombie);
		}
		renderer.processTerrain(terrain);
		renderer.processTerrain(terrain2);

		for (Entity entity : entities) {
			renderer.processEntity(entity);
		}

		for (Entity entity2 : entities2) {
			renderer.processEntity(entity2);
		}

		renderer.processEntity(entityArma);

		for (Bala tiro : balas) {
			renderer.processEntity(tiro);
		}

		renderer.render(light, player.getCamera());
		guiRenderer.render(guis);
	}

	private void loop() {
		while (!Display.isCloseRequested() && cont < 10000) {
			if (state == State.PLAYING)
				playing();
			else if (state == State.WIN) {
				just_render();
				cont++;
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					guis.remove(ganhou);
					reset();
				}
			} else if (state == State.LOSE) {
				just_render();
				cont++;
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					guis.remove(ganhou);
					reset();
				}
			}

			DisplayManager.updateDisplay();
		}

	}

	private void clear() {
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	public static void main(String[] args) {
		MainGameLoop game = new MainGameLoop();
		game.init();
		game.loop();
		game.clear();
	}

}