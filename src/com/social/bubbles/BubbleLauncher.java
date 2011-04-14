package com.social.bubbles;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.social.bubbles.R;
import com.social.bubbles.socialbubbles3.MySystem.CreateBubbleHandler;
import com.social.bubbles.tools.CircleCrop;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class BubbleLauncher {
    
    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
    private static HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
    
    private static File cacheDir;
    private static Context context;
    
    static PhotoLoaderThread photoLoaderThread;
    
    static BubbleLauncher mBubbleLauncher;
    
    static CreateBubbleHandler createBubbleHandler;
    
    public BubbleLauncher(Context context){
    	this.context = context;
    	this.mBubbleLauncher = this;
        //Make the background thread low priority. This way it will not affect the UI performance
        photoLoaderThread = new PhotoLoaderThread();
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"SocialBubbles/Pictures");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public static Bitmap GetDefaultImage(String type){
    	Bitmap bitmap;
    	//change these
    	if(type.equalsIgnoreCase(Bubble.FB_FRIENDS_EVENT)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_SPECIAL)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TODO)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else{
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	return bitmap;
    }
    
    public static Bitmap getImage(String url, String type){
    	if(cache.containsKey(url))
    			return cache.get(url);
		return cache.get(GetDefaultImage(type));
    }

    
    final int stub_id=R.drawable.stub;
    public static void RefreshBubbles()
    {
		Log.d("Bubble Launcher: Called RefreshBubbles", "");

        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW){
			Log.d("Bubble Launcher: Starting PhotoLoader Thread", "");

        	photoLoaderThread.start();
        }
    }
    
	class PhotoLoaderThread extends Thread {
	     public void run() {
				Log.d("Bubble Launcher: Started PhotoLoader Thread", "");
	    	 while(true){
	    		//Clear all bubbles or something to make room
		        while(!BubbleManager.displayQueue.isEmpty()){
					Log.d("Bubble Launcher: Downloading from Display Queue", "");

		        	Bitmap bitmap;
		        	String nextId = BubbleManager.displayQueue.remove();
		        	Bubble nextBubble = BubbleManager.cache.get(nextId);
		        	if(nextBubble.getImageUrl() == null || nextBubble.getImageUrl().equals("n/a")){
		        		bitmap = GetDefaultImage(nextBubble.getType());
	                    Log.d("Bubble Launcher: ", "Null URL - Default image used ");

		        	}
		        	else if(cache.containsKey(nextBubble.getImageUrl())){
		        		bitmap = (cache.get(nextBubble.getImageUrl()));
	                    Log.d("Bubble Launcher: ", "Picture taken from cache ");

		        	}
		        	else{
		        		bitmap=getBitmap(nextBubble.getImageUrl());
		                if(bitmap!=null){
		                	cache.put(nextBubble.getImageUrl(), bitmap);
		                    Log.d("Bubble Launcher: ", "Picture downloaded and cached ");

		                } else{
			        		bitmap = GetDefaultImage(nextBubble.getType());
		                    Log.d("Bubble Launcher: ", "Picture download failed ");
		                }
		        		//queuePhoto(nextBubble.getImageUrl());
		        	}

		        	Bitmap bubbleBit = editBubbleImage(nextBubble, bitmap);
                	cache.put(nextBubble.getId(), bubbleBit);

                	if(createBubbleHandler!=null)
                		launchBubble(nextBubble, bubbleBit);
		        }
	        }
	     }
	}
	
	private Bitmap editBubbleImage(Bubble bubble, Bitmap bitmap){
	    CircleCrop cc = new CircleCrop(context.getResources());

		//bitmap=cc.overlayBubble(bitmap);
	    
	    bitmap = cc.processBubble(bitmap, 160);
	    
		if(bubble.getType().contains("fb"))
			bitmap=cc.overlayFB(bitmap);
		else
			bitmap=cc.overlayFS(bitmap);

		return bitmap;
	}
	private void launchBubble(Bubble bubble, Bitmap bitmap){
		//IMPLEMENT
        Log.d("Bubble Launcher: ", "Launch bubble called ");

        int NEW_BUBBLE = 0;
        Message updateMessage = Message.obtain();
        updateMessage.what = NEW_BUBBLE;
        updateMessage.obj = bubble.getId();	//change eventually
        updateMessage.setTarget(createBubbleHandler);
        updateMessage.sendToTarget();
    	Log.d("WTF description is null ID=", bubble.getId());

      	Log.d("SENT NEW BUBBLE MESSAGE TO UPDATE WALLPAPER : ", bubble.getDescription());
	}
	
	public static void setCreateBubbleHandler(CreateBubbleHandler cbh){
		createBubbleHandler = cbh;
	}
    
    private Bitmap getBitmap(String url) 
    {
        Log.d("Bubble Launcher: ", "Getting picture from: " + url);
    	
    	//I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        File f=new File(cacheDir, filename);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            BufferedInputStream is=new BufferedInputStream(new URL(url).openConnection().getInputStream());
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           Log.d("Image Loader: ", "FAILED getting: " + url);
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=100;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    public void clearCache() {
        //clear memory cache
        cache.clear();
        
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }
    
//  private void queuePhoto(String url)
//  {
//      //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
//      photosQueue.Clean(url);
//      PhotoToLoad p=new PhotoToLoad(url);
//      synchronized(photosQueue.photosToLoad){
//          photosQueue.photosToLoad.push(p);
//          photosQueue.photosToLoad.notifyAll();
//      }
//      
//      //start thread if it's not started yet
//      if(photoLoaderThread.getState()==Thread.State.NEW)
//          photoLoaderThread.start();
//  }
    
    
//    //Task for the queue
//    private class PhotoToLoad
//    {
//        public String url;
//        public PhotoToLoad(String u){
//            url=u; 
//        }
//    }
//    
//    PhotosQueue photosQueue=new PhotosQueue();
//    
//    public void stopThread()
//    {
//        photoLoaderThread.interrupt();
//    }
//    
//    //stores list of photos to download
//    class PhotosQueue
//    {
//        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
//        
//        //removes all instances of this Bubble
//        public void Clean(String url)
//        {
//            for(int j=0 ;j<photosToLoad.size();){
//                if(photosToLoad.get(j).url.equalsIgnoreCase(url))
//                    photosToLoad.remove(j);
//                else
//                    ++j;
//            }
//        }
////    }
//    
//    class PhotosLoader extends Thread {
//        public void run() {
//            try {
//                while(true)
//                {
//                    //thread waits until there are any images to load in the queue
//                    if(photosQueue.photosToLoad.size()==0)
//                        synchronized(photosQueue.photosToLoad){
//                            photosQueue.photosToLoad.wait();
//                        }
//                    if(photosQueue.photosToLoad.size()!=0)
//                    {
//                        PhotoToLoad photoToLoad;
//                        synchronized(photosQueue.photosToLoad){
//                            photoToLoad=photosQueue.photosToLoad.pop();
//                        }
//                        Bitmap bmp=getBitmap(photoToLoad.url);
//                        cache.put(photoToLoad.url, bmp);
//                        
////                        Object tag=photoToLoad.imageView.getTag();
////                        if(tag!=null && ((String)tag).equals(photoToLoad.url)){
////                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
////                            Activity a=(Activity)photoToLoad.imageView.getContext();
////                            a.runOnUiThread(bd);
////                        }
//                    }
//                    if(Thread.interrupted())
//                        break;
//                }
//            } catch (InterruptedException e) {
//                //allow thread to exit
//            }
//        }
//    }
//    
//    PhotosLoader photoLoaderThread=new PhotosLoader();
//    
//    //Used to display bitmap in the UI thread
//    class BitmapDisplayer implements Runnable
//    {
//        Bitmap bitmap;
//        ImageView imageView;
//        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
//        public void run()
//        {
//            if(bitmap!=null)
//                imageView.setImageBitmap(bitmap);
//            else
//                imageView.setImageResource(stub_id);
//        }
//    }



}
