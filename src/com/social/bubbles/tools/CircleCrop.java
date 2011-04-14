package com.social.bubbles.tools;

import com.social.bubbles.Bubble;
import com.social.bubbles.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Region;

public class CircleCrop {
	private Resources res;
    public CircleCrop(Resources res){
    	this.res = res;
    }
    public Bitmap processBubble(Bitmap b, int dimension){
    	Bitmap r = squareCropBitmap(b, dimension);
    	r = circleCropBitmap(r);
    	r = overlayBubble(r);
    	return r;
    }
    public Bitmap circleCropResource(int id){
    	Bitmap b = BitmapFactory.decodeResource(res,id);
    	b = circleCropBitmap(b);
    	return b;
    }
    public Bitmap circleCropBitmap(Bitmap b){
    	Bitmap bg = Bitmap.createBitmap(b.getWidth(),b.getHeight(),Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(bg);
    	c.drawBitmap(b, 0, 0, new Paint());
    	
    	Path p = new Path();
    	p.addCircle(b.getWidth()/2F,b.getHeight()/2F,b.getWidth()/2F,Path.Direction.CW);
    	c.clipPath(p,Region.Op.DIFFERENCE);
    	c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
    	return bg;
    }
    public Bitmap squareCropBitmap(Bitmap b, int dimension){
    	Bitmap bg;
    	if(b.getWidth()>b.getHeight()){
    		bg = Bitmap.createBitmap(b, ((b.getWidth()-b.getHeight())/2), 0, b.getHeight(), b.getHeight());
        	
    	}else{
        	bg = Bitmap.createBitmap(b, 0, ((b.getHeight()-b.getWidth())/8), b.getWidth(), b.getWidth());

    	}

    	bg = resizeBitmap(bg, dimension, dimension);
    	
    	return bg;
    }
    public Bitmap resizeBitmap(Bitmap b, int newWidth, int newHeight){
    	 int width = b.getWidth();
         int height = b.getHeight();
        
         // calculate the scale - in this case = 0.4f
         float scaleWidth = ((float) newWidth) / width;
         float scaleHeight = ((float) newHeight) / height;
        
         // createa matrix for the manipulation
         Matrix matrix = new Matrix();
         // resize the bit map
         matrix.postScale(scaleWidth, scaleHeight);
         // rotate the Bitmap
         //matrix.postRotate(45);
  
         // recreate the new Bitmap
         Bitmap resizedBitmap = Bitmap.createBitmap(b, 0, 0,
                           width, height, matrix, true);
    
         return resizedBitmap;
    }
    public Bitmap overlayBubble(Bitmap b){
    	Bitmap bubble = BitmapFactory.decodeResource(res, R.drawable.bubble);
    	bubble = resizeBitmap(bubble,b.getWidth(),b.getHeight());
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(bubble, 0, 0, new Paint());
    	return f;
    }
    public Bitmap overlayFB(Bitmap b){
    	Bitmap icon = BitmapFactory.decodeResource(res, com.facebook.android.R.drawable.facebook_icon);
    	icon = resizeBitmap(icon,b.getWidth()/4,b.getHeight()/4);
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(icon, 0, 0, new Paint());
    	return f;
    }
    public Bitmap overlayFS(Bitmap b){
    	Bitmap icon = BitmapFactory.decodeResource(res, com.frublin.androidoauth2.R.drawable.foursquareicon);
    	icon = resizeBitmap(icon,b.getWidth()/4,b.getHeight()/4);
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(icon, 0, 0, new Paint());
    	return f;
    }
    
    public static Bitmap GetDefaultImage(String type){
    	Bitmap bitmap = null;
    	//change these
    	if(type.equalsIgnoreCase(Bubble.FB_FRIENDS_EVENT)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_SPECIAL)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TODO)){

    	}
    	else{

    	}
    	return bitmap;
    }
}