package com.social.bubbles.socialbubbles3;
 
import java.util.LinkedList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.social.bubbles.BubbleLauncher;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


/**
 * @author Mike NTG
 *Adapted from:
 */
 
/**
 * Android Live Wallpaper painting thread Archetype
 * @author antoine vianey
 * GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class MySystem extends Thread {
 
    /** Reference to the View and the context */
    private SurfaceHolder surfaceHolder;
    private Context context;
 
    /** State */
    private boolean wait;
    private boolean run;
 
    /** Dimensions */
    private int width;
    private int height;
 
    /** Time tracking */
    private long previousTime;
    private long currentTime;
	
	/** Option display **/
	private boolean option;
	
	/** particle point **/
	//Initial position
	private float pBaseX;
	private float pBaseY;
	// Position during translation
	// private float pX;
	// private float pY;
	
	/** Particle **/
	private myParticle mParticle;
	private  LinkedList<myParticle> mParticleSystem;
	
	private myParticle mParticleOp1;
	private myParticle mParticleOp2;
	private myParticle[] mParticleOp;
	
	private int[] images;
	
	private Random ran;
	
	private int particleIndex;
	
	private int numBubbles;
	
	Bitmap img;
	
	Resources res;
	
	SocialBubblesService socialEngine;
	
    CreateBubbleHandler createBubbleHandler;
	
	

 
    public MySystem(SurfaceHolder surfaceHolder, 
            Context context, int width, int nBubbles, Resources res, SocialBubblesService se) {
    	
    	createBubbleHandler = new CreateBubbleHandler();
    	BubbleLauncher.setCreateBubbleHandler(createBubbleHandler);
    	
    	myParticle p;
        // keep a reference of the context and the surface
        // the context is needed if you want to inflate
        // some resources from your livewallpaper .apk
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        // don't animate until surface is created and displayed
        this.wait = true;
        
        this.res= res;
        ran= new Random();
        
        socialEngine= se;
        
        //getting the dimensions
        this.width= surfaceHolder.getSurfaceFrame().right;
        this.height= surfaceHolder.getSurfaceFrame().bottom;
        
	    
	    //loading images to the array
	    images= new int[10];
	    images[0]= R.drawable.mike;
	    images[1]= R.drawable.geoffrey;
	    images[2]= R.drawable.kai;
	    images[3]= R.drawable.wei;
	    images[4]= R.drawable.cis350;
	    images[5]= R.drawable.facebook;
	    images[6]= R.drawable.androidvsios;
	    images[7]= R.drawable.android;
	    images[8]= R.drawable.android2;
	    images[9]= R.drawable.ex;
	    
        
        //getting the picture
        //img= bitmp;
		
		//Create the particles
       
	    
		//mParticle= new myParticle(250,400,0xFFFFFFFF,width,width, "bubble");
	    img= BitmapFactory.decodeResource(res, R.drawable.icon_phone);		// res.getDrawable(R.drawable.bubble);
		mParticleOp1= new myParticle(150,200,0xFFFFFFFF,img.getWidth(),img.getHeight(), "option 1",img);
		img= BitmapFactory.decodeResource(res, R.drawable.sms);		// res.getDrawable(R.drawable.bubble);
		mParticleOp2= new myParticle(50,500,0xFFFFFFFF,img.getWidth(),img.getHeight(), "option 2",img);
		mParticleOp= new myParticle[2];
		mParticleOp[0]= mParticleOp1;
		mParticleOp[1]= mParticleOp1;
		mParticleSystem= new LinkedList<myParticle>();
		//mParticleSystem.add(mParticle);
		
		//CREATES THE BUBBLES
		//INPUT: SYSTEM, NUMBER OF BUBBLES
		//createInitBubbles(mParticleSystem,nBubbles);
		//createInitBubbles(nBubbles);
		
		
		//Creates the initial bubbles: DIRECT METHOD
//		for (int i= 0; i<nBubbles; i++){
//			int w= ran.nextInt(1000);
//			int h= ran.nextInt(1000);
//			img= BitmapFactory.decodeResource(res, images[i]);
//			p= new myParticle(w,h,0xFFFFFFFF,img.getWidth(),img.getHeight(), "bubble",img);
//    		this.mParticleSystem.add(p);   		
//    	}
		createBubbles(nBubbles);
		
		
		
		option= false;
		
		// Initial Position of particle
		pBaseX= width/2;
		pBaseY= 0;
		// Position during translation
		// private int pX;
		// private int pY;
		
		particleIndex = -1;
		
		
		numBubbles = nBubbles;
		
//		if(mParticleSystem.size()>= 2){
//			p= new myParticle(150,0,0xFFFFFFFF,width,width, "bubble");
//			mParticleSystem.add(p);
//		}
		
		
    }
 
    /**
     * Pauses the live wallpaper animation
     */
    public void pausePainting() {
        this.wait = true;
        synchronized(this) {
            this.notify();
        }
    }
 
    /**
     * Resume the live wallpaper animation
     */
    public void resumePainting() {
        this.wait = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    /**
     * Stop the live wallpaper animation
     */
    public void stopPainting() {
        this.run = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    @Override
    public void run() {
        this.run = true;
        Canvas c = null;
        while (run) {
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
					
				
				
                    currentTime = System.currentTimeMillis();
                    //if (!option){
                    	updatePhysics(mParticleSystem);
                    //}
                    if (!option){
                    	doDraw(c, mParticleSystem);
                    }
                    
                    //Draw only if a particle was touched
                    if (option){
                    	myParticle touchParticle = mParticleSystem.get(particleIndex); //---------------------------------------------------
                    	doDrawTouchPoint(c,touchParticle);
					
						doDrawOption(c,mParticleOp1,mParticleOp2);
						doCheckIfOption(c,touchParticle,mParticleOp1,mParticleOp2);
					}
					if(currentTime-previousTime> 20){ // not sure if the  time is correct------------------------
						option= false;
					}
                    previousTime = currentTime;
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            // pause if no need to animate
            synchronized (this) {
                if (wait) {
                    try {
                        wait();
                    } catch (Exception e) {}
                }
            }
			
			// check if schedule next drawing here-.--------------------
        }
    }
 
   

	

	/**
     * Invoke when the surface dimension change
     */
    public void setSurfaceSize(int width, int height) {
        this.width = width;
        this.height = height;
        synchronized(this) {
            this.notify();
        }
    }
 
    // ===========================================================
	// Do Methods
	// ===========================================================
    
    /**
     * Invoke while the screen is touched
     */
    public void doTouchEvent(MotionEvent event) {
        // handle the event here
        // if there is something to animate
        // then wake up
        this.wait = false;
        int index= -1;
		
		// modificar las opciones de display entre option---------------------------------
		
        
        //gets the index of the particle touched on screen, returns -1 otherwise
        index= isInParticleSystem(event.getX(),event.getY(), mParticleSystem);
        //if touched a particle then index is >= 0
        option= index >= 0;
        

       
        //move the selected particle around
        if (option){
	    	synchronized(this.mParticleSystem){
	    		myParticle p = mParticleSystem.get(index);
//				p.setX(event.getX());   // it does not center the picture.
//				p.setY(event.getY());
	    		
	    		//it centers the picture in this point
	    		p.setX(event.getX()-(p.getWidth())/2);   
				p.setY(event.getY()-(p.getHeight()/2));
	    		
			}
        }
        
        particleIndex= index;
		
//		//updating the base position  //only when animating the particle to restore position------------------
//		synchronized(this){
//			pBaseX= event.getX();
//		}
//		
//		synchronized(this){
//			pBaseY= event.getY();
//		}
//		
//        synchronized(this) {
//            notify();
//        }
    }
 
    

	/**
     * Do the actual drawing stuff : DRAWAS ALL THE PARTICLES
     */
    private void doDraw(Canvas canvas, LinkedList<myParticle> pSystem) {
//    	Resources res = 
//    	Bitmap bm= img.overlayBubble(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
		 Paint paint = new Paint();
		 //paint.setColor(mParticle.getColor());
		 paint.setColor(0xABCD0000);
		 canvas.drawColor(0xff000000);
		 
		 
		 //CALL AND DISPLAY NEXT BUBBLE HERE
		 
		 for (myParticle p: (LinkedList<myParticle>) pSystem.clone()){
			 //canvas.drawCircle(p.getX(), p.getY(), p.getWidth(), paint); 
			 //canvas.drawRect(p.getX(), p.getY(), p.getX()+p.getWidth(), p.getY()+p.getHeight(), paint); // draws the bounding bound, or touch area of the object
			 canvas.drawBitmap(p.getImg(), p.getX(), p.getY(), new Paint());
			 
		 }
		 
		 
		 
		 
	}
    
    //DRAWS THE SUB-BUBBLES
    private void doDrawOption(Canvas c, myParticle op1,
			myParticle op2) {
		
    	Paint paint = new Paint();
		paint.setColor(op1.getColor());
		//c.drawColor(0xff000000);
		
//		c.drawCircle(op1.getX(), op1.getY(), op1.getWidth(), paint);
//		c.drawCircle(op2.getX(), op2.getY(), op2.getWidth(), paint);
		
		//DRAW THE SUB BUBBLES HERE
		
		c.drawBitmap(op1.getImg(), op1.getX(), op1.getX(), new Paint());
		c.drawText("Name", width/2, 0, paint);
		//c.drawRect(op1.getX(), op1.getY(), op1.getX()+op1.getWidth(), op1.getY()+op1.getHeight(), paint); // draws the bounding bound, or touch area of the object
		c.drawBitmap(op2.getImg(), op2.getX(), op2.getX(), new Paint());
		//c.drawRect(op2.getX(), op2.getY(), op2.getX()+op2.getWidth(), op2.getY()+op2.getHeight(), paint); // draws the bounding bound, or touch area of the object
		
	}
    
    private void doDrawTouchPoint(Canvas c, myParticle tp){
		Paint paint = new Paint();
		paint.setColor(tp.getColor());
		c.drawColor(0xff000000);
		
		//c.drawCircle(tp.getX(), tp.getY(), tp.getWidth(), paint);
		c.drawBitmap(tp.getImg(), tp.getX(), tp.getY(), new Paint());
	}
    
    //EXECUTES THE OPTION ACTION: ON DROP
    private void doCheckIfOption(Canvas c, myParticle p, myParticle op1, myParticle op2) {
    	//boolean result= false;
    	Paint paint = new Paint();
		paint.setColor(p.getColor());
		paint.setTextSize(34);
		
		
		
		
		//result= isInSystem(p.getX(),p.getY(),op1);
		
		//CHECKS WHICH OPTION TO EXECUTE AND LAUNCHS IT
		
//		if(isInSystem(p.getX(),p.getY(),op1)){  //does not apply to pictures
		if(isInSystem(p.getX()+(p.getWidth()/2),p.getY()+(p.getHeight()/2),op1)){
//			c.drawColor(0xff000000);
//			c.drawText("I am calling", 150, 400, paint);
			String url = "http://www.facebook.com";
    	    Intent i = new Intent(Intent.ACTION_VIEW);
    	    i.setData(Uri.parse(url));
    	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    socialEngine.startActivity(i);
			
		    
		}else if (isInSystem(p.getX()+(p.getWidth()/2),p.getY()+(p.getHeight()/2),op2)){
			c.drawColor(0xff000000);
			c.drawText("I am texting", 150, 400, paint);
		}
		
	}
    
    // ===========================================================
	// Physics
	// ===========================================================
 
    /**
     * Update the animation, sprites or whatever.
     * If there is nothing to animate set the wait
     * attribute of the thread to true
     */
    private void updatePhysics(LinkedList<myParticle> pSystem) {
        // if nothing was updated :
        // this.wait = true;
    	
    	
    	for (myParticle p : pSystem){
    	
	    	//float x= mParticle.getX();
	    	float y= p.getY();
	    	
	    	//x++;
	    	y++;
	    	if (y> height ){
	    		p.setY(pBaseY);
	    		p.setX(ran.nextInt(width));
	    	}else {
	    		p.setY(y);
	    	}
    	}
    	
//    	float x= mParticle.getX();
//    	float y= mParticle.getY();
//    	
//    	//x++;
//    	y++;
//    	if (y> height ){
//    		mParticle.setY(pBaseY);
//    		mParticle.setX(ran.nextInt(width));
//    	}else {
//    		mParticle.setY(y);
//    	}
    	
    }
	
	
	
    // ===========================================================
	// additional Methods
	// ===========================================================

	// changes the particle width and height
	public void setWidth(int w) {
		for (myParticle p : this.mParticleSystem){
			p.setHeight(w);
			p.setWidth(w);
		}
	}
	
	//checks if the touch position is within a particle
    private boolean isInSystem(float x, float y, myParticle p) {
		boolean result= false;
		
		result = p.isInParticle(x, y);
		
		return result;
	}
    
    //checks if the touch position is within a particle
    private int isInParticleSystem(float x, float y, LinkedList<myParticle> pSystem) {
		int index= -1;
		
		for (myParticle p: pSystem){
			if (p.isInParticle(x, y)){
				return pSystem.indexOf(p);
			}
		}		
		return index;
	}
 
    //creates the initial bubbles
    //private void createInitBubbles(LinkedList<myParticle> pSystem, int n){
    private void createInitBubbles(int n){
    	myParticle p;
    	
    	//pSystem = this.mParticleSystem;
//    	for (int i= 0; i<n; i++){
//    		p= new myParticle(150,200,0xFFFFFFFF,width,width, "bubble");
//    		this.mParticleSystem.add(p);
//    		
//    	}
    	p= new myParticle(150,200,0xFFFFFFFF,width,width, "bubble",img);
    	mParticleSystem.add(p);
    }
    
    public void createBubbles (int n){
    	myParticle p;

		for (int i= 0; i<n; i++){
			int w= ran.nextInt(1000);
			int h= ran.nextInt(1000);
			img= BitmapFactory.decodeResource(res, images[i]);
			p= new myParticle(w,h,0xFFFFFFFF,img.getWidth(),img.getHeight(), "bubble",img);
			this.mParticleSystem.add(p);   		
		}
    	
//    	//createInitBubbles(this.mParticleSystem,n);
//    	//createInitBubbles(n);
//    	myParticle p;
//    	mParticleSystem.clear();
//    	for (int i= 0; i<n; i++){
//    		int w= 100;//ran.nextInt(width);
//			int h= 100;//ran.nextInt(height);
//			img= BitmapFactory.decodeResource(res, images[i]);
//			p= new myParticle(w,h,0xFFFFFFFF,width,width, "bubble",img);
//    		this.mParticleSystem.add(p);   		
//    	}
    }
    
    //by kai
    public void createBubble(String id){
    	myParticle p;
    		int w= ran.nextInt(width);
			int h= ran.nextInt(height);
			Bitmap pic = BubbleLauncher.getImage(id, BubbleManager.cache.get(id).getType());
			p= new myParticle(w,h,0xFFFFFFFF,pic.getWidth(),pic.getHeight(), "bubble", pic);
    		this.mParticleSystem.add(p);
    	if(this.mParticleSystem.size() > BubbleManager.DISPLAY_QUEUE_SIZE)
    		this.mParticleSystem.remove();
    }
    
    public class CreateBubbleHandler extends Handler {
        private static final int NEW_BUBBLE = 0;

		public void handleMessage(Message msg) {
                switch (msg.what) {
                case NEW_BUBBLE:
	                   createBubble((String) msg.obj);      
                }  
		}
    }

    
    public void onChangeNumberBubbles(int num){
    	BubbleManager.DISPLAY_QUEUE_SIZE = num;
    	//get num bubbles
    	BubbleManager.queueQualifyingBubbles();
    }
}