package com.social.bubbles.socialbubbles3;

import android.graphics.Bitmap;

/**
 * @author Mike NTG
 *
 */
 
public class myParticle {

	protected float x;
	protected float y;
	protected int color;
	protected int width;
	protected int height;
	protected String name;
	protected String url;
	protected Bitmap img;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public myParticle(float x, float y, int color, int width, int height, String name,Bitmap b) {
//		this.x = x-(width/2);
//		this.y = y-(height-2);
		this.x = x;
		this.y = y;
		this.color = color;
		this.height = height;
		this.width = width;
		this.setName(name);
		this.img= b;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String getName() {
		return name;
	}
	
	public Bitmap getImg() {
		return img;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setColor(int c) {
		this.color = c;
	}
	
	public void setWidth(int w) {
		this.width = w;
	}
	
	public void setHeight(int h) {
		this.height = h;
	}
	
	public void setImg (Bitmap b){
		img= b;
	}
	
	public boolean isInParticle(float x, float y){
		boolean result= false;
		
		//Check the bounding box. check if it is circle or square, bounding box me be different
		
//		if ((x>= this.x-width)&& (x<= this.x+width)){
//			if ((y>= this.y-height)&& (y<= this.y+height)){
//				result= true;
//			}
//			
//		}
		
		
		//THIS ONE ONLY VALID FOR BUBBLES
		//result= ((x>= this.x-width)&& (x<= this.x+width)) && ((y>= this.y-height)&& (y<= this.y+height)); // this if (x,Y)= center and width is radius
		
		result= ((x>= this.x)&& (x<= this.x+width)) && ((y>= this.y)&& (y<= this.y+height)); // this if (x,y)= top/left width ad height
		
		
		
		
		return result;
	}
	
	

}
