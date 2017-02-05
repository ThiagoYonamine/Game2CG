package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRender;
import renderEngine.OBJLoader;
import terrains.Terrain;
import toolBox.Time;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import entities.Bala;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Zombie;


public class MainGameLoop {

	private final static int MIN_TREE_HEIGHT = 1;
	private final static int MAX_TREE_HEIGHT = 4;


	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
	    
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		RawModel model = OBJLoader.loadObjModel("pine", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("pine")));
		staticModel.getTexture().setHasTransparency(true);
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities.add(new Entity(staticModel,
					new Vector3f(random.nextFloat() * 800 - 400, 0, (random.nextFloat() * -600) - 30), 0, 0, 0, scale));
		}

		RawModel model2 = OBJLoader.loadObjModel("fern", loader);
		TexturedModel staticModel2 = new TexturedModel(model2, new ModelTexture(loader.loadTexture("fern")));
		staticModel2.getTexture().setHasTransparency(true);
		staticModel2.getTexture().setUseFakeLighting(true);

		List<Entity> entities2 = new ArrayList<Entity>();
		for (int i = 0; i < 200; i++) {
			float scale = MIN_TREE_HEIGHT + (float) Math.random() * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
			entities2.add(new Entity(staticModel2,
					new Vector3f(random.nextFloat() * 800 - 400, -1f, random.nextFloat() * -600), 0, 0, 0, scale / 10));
		}
		/*
		 * RawModel model_tiro = OBJLoader.loadObjModel("pine", loader);
		 * TexturedModel tx_tiro = new TexturedModel(model_tiro, new
		 * ModelTexture(loader.loadTexture("pine"))); List<Entity> tiros = new
		 * ArrayList<Entity>(); tiros.add(new Entity(tx_tiro,new Vector3f(1, 1f,
		 * 1), 0, 0, 0, 1));
		 */

		RawModel dragonOBJ = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragon = new TexturedModel(dragonOBJ, new ModelTexture(loader.loadTexture("verde")));
		ModelTexture dragon_texture = dragon.getTexture();
		// Reflexo
		dragon_texture.setShineDamper(10); // tipo do material
		dragon_texture.setReflectivity(50); // reflexo

		Entity entityDragon = new Entity(dragon, new Vector3f(0, 0, -40), 0, 0, 0, 1);

		RawModel model_arma = OBJLoader.loadObjModel("arma", loader);
		TexturedModel tx_arma = new TexturedModel(model_arma, new ModelTexture(loader.loadTexture("arma")));
		ModelTexture texture_arma = tx_arma.getTexture();
		// Reflexo
		texture_arma.setShineDamper(50); // tipo do material
		texture_arma.setReflectivity(20); // reflexo

		Entity entityArma = new Entity(tx_arma, new Vector3f(110, 10, -50), 0, 0, 0, 0.5f);
		entityArma.increaseRotation(180, 0, 0);

		Light light = new Light(new Vector3f(0, 50, -30), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

		MasterRender renderer = new MasterRender();
		TexturedModel player_model = tx_arma;

		Player player = new Player(player_model, new Vector3f(0, 5, 0), 0, 180, 0, 0.5f);
		player.increaseRotation(180, 0, 0);

		RawModel model_zombie = OBJLoader.loadObjModel("Slasher", loader);
		TexturedModel tx_zombie = new TexturedModel(model_zombie, new ModelTexture(loader.loadTexture("Slasher")));
		// Zombie zombie = new Zombie(tx_zombie, new Vector3f(0, 5, -100), 0, 0,
		// 0, 5);
		List<Zombie> zombies = new ArrayList<Zombie>();
		for (int i = 0; i < 20; i++) {
			zombies.add(
					new Zombie(tx_zombie, new Vector3f(random.nextFloat() * 800 - 400, 5, random.nextFloat() * -600), 0,
							0, 0, 5, random.nextFloat() % 1));
		}

		RawModel model_bala = OBJLoader.loadObjModel("bullet2", loader);
		TexturedModel tx_bala = new TexturedModel(model_bala, new ModelTexture(loader.loadTexture("mud")));
		// tx_bala.getTexture().setHasTransparency(true);

		List<Bala> balas = new ArrayList<Bala>();
		// hide the mouse
		Mouse.setGrabbed(true);

		boolean ptiro = true;
		while (!Display.isCloseRequested()) {

			player.move();
			for (Zombie zombie : zombies) {
				// zombie.move(player.getPosition(),player.getRotY());
				renderer.processEntity(zombie);
			}

			//renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(entityDragon); // 0 0 -40
			// teste colisao
			float px = player.getPosition().getX();
			float pz = player.getPosition().getZ();
			if (pz <= -35 && px >= -5 && px <= 5 && pz >= -45) {
				if (pz < -40)
					player.increasePosition(0, 0, -1);
				else
					player.increasePosition(0, 0, 1);

			}
			for (Entity entity : entities) {
				Vector3f v = entity.getPosition();
				renderer.processEntity(entity);

				// todo ??? colisõa
		
				if (pz <= v.getZ() + 3 && px >= v.getX() - 3 && px <= v.getX() + 3 && pz >= v.getZ() - 3) {
					if (pz < v.getZ())
						player.increasePosition(0, 0, -2);
					else
						player.increasePosition(0, 0, 2);
					if (px < v.getX())
						player.increasePosition(-2, 0, 0);
					else
						player.increasePosition(2, 0, 0);

				}

			}
			for (Entity entity2 : entities2) {
				renderer.processEntity(entity2);
			}

			// entityDragon.increaseRotation(0, 1, 0);

			entityArma
					.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z));
			entityArma.setRotY(player.getRotY() * -1);
			renderer.processEntity(entityArma);
			
			if (Mouse.isButtonDown(0) && Time.getDelta()>100) {

				Bala b = new Bala(tx_bala,
						new Vector3f(player.getPosition().x, player.getPosition().y + 3, player.getPosition().z), 0,
						player.getRotY(), 0, 0.1f);

				b.atira(player.getRotY());
				balas.add(b);
				// ptiro=false;
			}

			balas.removeIf(new Predicate<Bala>() {
				@Override
				public boolean test(Bala x) {
					return x.getTempo() > 100;
				}
			});

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