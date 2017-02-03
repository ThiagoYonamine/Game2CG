package entities;
 
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
 
public class Camera {
     
    private Vector3f position = new Vector3f(0,20,20);
    private float pitch = 10;
    private float yaw ;
    private float roll;
     
    public Camera(){}
     
    public void move(){
        
    }
 
    public Vector3f getPosition() {
        return position;
    }
 
    public float getPitch() {
        return pitch;
    }
 
    public float getYaw() {
        return yaw;
    }
 
    public float getRoll() {
        return roll;
    }
     
     
 
}