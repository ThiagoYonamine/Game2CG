package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Bala extends Entity{
	private double direcao;
	private int tempo = 0;

	public Bala(TexturedModel model, Vector3f position, float rotX, float rotY,
			float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);

		
	}
	public void atira( float roty){
		direcao = Math.toRadians(roty);
		//increaseRotation(0,0,90-(float) direcao);
		
	}
	public void move() {
		increasePosition((float) Math.sin(direcao), 0, (float) Math.cos(direcao));
		tempo++;
	}
	
	public int getTempo(){
		return tempo;
	}

}
