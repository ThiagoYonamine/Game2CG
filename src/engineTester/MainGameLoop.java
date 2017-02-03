package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRender;
import renderEngine.OBJLoader;
import terrains.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import entities.Entity;
import entities.Light;
import entities.Player;

public class MainGameLoop {

	private final static int MIN_TREE_HEIGHT = 4;
	private final static int MAX_TREE_HEIGHT = 12;

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 500; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities.add(new Entity(staticModel,
					new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, scale));
		}

		RawModel model2 = OBJLoader.loadObjModel("fern", loader);
		TexturedModel staticModel2 = new TexturedModel(model2, new ModelTexture(loader.loadTexture("fern")));
		staticModel2.getTexture().setHasTransparency(true);
		staticModel2.getTexture().setUseFakeLighting(true);

		List<Entity> entities2 = new ArrayList<Entity>();
		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities2.add(new Entity(staticModel2,
					new Vector3f(random.nextFloat() * 800 - 400, -1f, random.nextFloat() * -600), 0, 0, 0, scale/10));
		}

		RawModel dragonOBJ = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragon = new TexturedModel(dragonOBJ, new ModelTexture(loader.loadTexture("verde")));
		ModelTexture dragon_texture = dragon.getTexture();
		// Reflexo
		dragon_texture.setShineDamper(50); // tipo do material
		dragon_texture.setReflectivity(200); // reflexo

		// Entity entityDragon = new Entity(dragon, new Vector3f(0, 0, -40), 0,
		// 0, 0, 1);

		Light light = new Light(new Vector3f(0, 50, -30), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

		MasterRender renderer = new MasterRender();
		TexturedModel player_model = dragon;

		Player player = new Player(player_model, new Vector3f(100, 0, -50), 0, 0, 0, 1);

		// hide the mouse
		Mouse.setGrabbed(true);

		while (!Display.isCloseRequested()) {
			player.move();

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			for (Entity entity2 : entities2) {
				renderer.processEntity(entity2);
			}
			// entityDragon.increaseRotation(0, 1, 0);
			// renderer.processEntity(entityDragon);
			renderer.render(light, player.getCamera());
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}