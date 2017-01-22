package engineTester;
 
import models.RawModel;
import models.TexturedModel;
 
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
 
import renderEngine.DisplayManager;
import renderEngine.Loader;
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
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);
         

        RawModel model = OBJLoader.loadObjModel("dragon", loader);
         
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("white")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(2);
        
        //entity (textura,new Vector3f(POSITION),ROTATION, SCALE);
        Entity entity = new Entity(staticModel, new Vector3f(0,-5,-25),0,0,0,1);
        //Luz (location, color)
         Light light =new Light(new Vector3f(0,0,-20),new Vector3f(1,1,1));
        
        Camera camera = new Camera();
         
        while(!Display.isCloseRequested()){
            entity.increaseRotation(0, 1, 0);
            camera.move();
            renderer.prepare();
            shader.start();  
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity,shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }
 
        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}