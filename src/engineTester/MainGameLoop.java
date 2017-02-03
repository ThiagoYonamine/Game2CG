package engineTester;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
import models.RawModel;
import models.TexturedModel;
 
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
import entities.Camera;
import entities.Entity;
import entities.Light;
 
public class MainGameLoop {
 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        
         TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
         TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
         TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
         TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
         TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
         TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
         
        RawModel model = OBJLoader.loadObjModel("tree", loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
        //ModelTexture staticModel_texture = staticModel.getTexture();
        //Reflexo
        //staticModel_texture.setShineDamper(100); // tipo do material
        //staticModel_texture.setReflectivity(1); //reflexo
        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<100;i++){
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,12));
        }
        
        RawModel model2 = OBJLoader.loadObjModel("fern", loader);
        TexturedModel staticModel2 = new TexturedModel(model2,new ModelTexture(loader.loadTexture("fern")));
        staticModel2.getTexture().setHasTransparency(true);
        staticModel2.getTexture().setUseFakeLighting(true);
        
        List<Entity> entities2 = new ArrayList<Entity>();
        for(int i=0;i<100;i++){
            entities2.add(new Entity(staticModel2, new Vector3f(random.nextFloat()*800 - 400,-1f,random.nextFloat() * -600),0,0,0,1));
        }
        
        
        RawModel dragonOBJ = OBJLoader.loadObjModel("zombie2", loader);
        TexturedModel dragon = new TexturedModel(dragonOBJ,new ModelTexture(loader.loadTexture("zombie")));
        ModelTexture dragon_texture = dragon.getTexture();
        //Reflexo
        dragon_texture.setShineDamper(100); // tipo do material
        dragon_texture.setReflectivity(1); //reflexo
       
        Entity entityDragon = new Entity(dragon,new Vector3f(0,10,-300),0,0,0,5);
        
        
        Light light = new Light(new Vector3f(0,20,-100),new Vector3f(0.5f,0.5f,0.4f));
      
         
        Terrain terrain = new Terrain(0,-1,loader,texturePack ,blendMap);
        Terrain terrain2 = new Terrain(-1,-1,loader,texturePack ,blendMap);
         
        Camera camera = new Camera();   
        MasterRender renderer = new MasterRender();

        while(!Display.isCloseRequested()){
            camera.move();
            
            
            light.setPosition(new Vector3f(camera.getPosition().x,camera.getPosition().y+20,camera.getPosition().z));
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            for(Entity entity2:entities2){
                renderer.processEntity(entity2);
            }

            //entityDragon.increaseRotation(0, 1, 0);
            entityDragon.increasePosition(0, 0, 0.5f);
            renderer.processEntity(entityDragon);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
 
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}