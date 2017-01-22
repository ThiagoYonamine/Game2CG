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
import renderEngine.Renderer;
import shaders.StaticShader;
import Textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;
 
public class MainGameLoop {
 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        //StaticShader shader = new StaticShader();
        //Renderer renderer = new Renderer(shader);
         
        //carrega o dragon
        RawModel model = OBJLoader.loadObjModel("dragon", loader);   
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("grass")));
        //set as propriedades da textura
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(2);
        
        
        //Cria o dragon
        //entity (textura,new Vector3f(POSITION),ROTATION, SCALE);
         Entity entity = new Entity(staticModel, new Vector3f(0,-5,-25),0,0,0,1);
        //Cria Luz (location, color)
         Light light =new Light(new Vector3f(200,200,100),new Vector3f(1,1,1));
        
        Camera camera = new Camera();
        
        List<Entity> allDragons = new ArrayList<Entity>();
        Random random = new Random();
        for(int i =0 ; i<10;i++){
        	float x = random.nextFloat()*50-20;
        	float y = random.nextFloat()*50-20;
        	float z = random.nextFloat()*-100;
        	allDragons.add(new Entity(staticModel, new Vector3f(x,y,z),random.nextFloat()*180f,random.nextFloat()*180f,0,1));
        }
        MasterRender renderer = new MasterRender();
        while(!Display.isCloseRequested()){
            
            camera.move();
            /* Tudo foi colocado no MasterRender
           // renderer.prepare();
           // shader.start();  
           // shader.loadLight(light);
            //shader.loadViewMatrix(camera);
           // renderer.render(entity,shader);
            //shader.stop();*/
            
            for (Entity dragon: allDragons){
            	dragon.increaseRotation(0, 1, 0);
            	renderer.processEntity(dragon);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        //shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}