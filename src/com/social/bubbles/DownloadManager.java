package com.social.bubbles;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.bubbles.location.MyLocation;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.social.bubbles.facebook.Session;
import com.social.bubbles.fbquery.FacebookObject;
import com.social.bubbles.fbquery.FacebookQuery;
import com.social.bubbles.fsquery.FoursquareObject;
import com.social.bubbles.fsquery.FoursquareQuery;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class DownloadManager extends Service {
	private static final String TAG = "		Bubbles Download Manager: ";
	
	
	
	private static HashMap<String, Bitmap> imageDB;
	
	private Service mService;
	
	protected static FacebookQuery fbQuery;
	protected static FoursquareQuery fsQuery;

	
	//Handlers
	private DownloadSignalHandler downloadSignalHandler; 	//starts new set of FB/FS downloads
	protected static Handler updateHandler;					//communicates data to UI

	//Fetching threads
	protected Thread fetchInfoThread;

	
	//Bubble Manager
	private static BubbleManager bubbleManager;

	
	@Override
	public void onCreate() {
		mService = this;

		bubbleManager = new BubbleManager(mService);

		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		downloadSignalHandler = new DownloadSignalHandler();
		MyLocation.setHandler(downloadSignalHandler);
		//imageDB = new HashMap<String, Bitmap>();
		
		fetchInfoThread = new FetchInfoThread();
	}
	
//downloads relevant facebook info
	private class FetchInfoThread extends Thread {
	
	      public void run() {
	    	  this.setPriority(MIN_PRIORITY);
    		  
    		  boolean needLogin = false;
	    	  if(fsQuery!=null && BubbleManager.DISPLAY_FOURSQUARE_BUBBLES){
	        	  if(fsQuery.isSessionValid()){
	        		  //retrieve all foursquare objects
		    		  Log.d("Download Manager: ", "retrieve all relevant Foursquare objects");

		    		  LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
		    		  bubbles.addAll((fsQuery.getFoursquareTodos()));
	        		  BubbleManager.add(bubbles);
	        		  
	        		  bubbles.clear();
		    		  bubbles.addAll((fsQuery.getFoursquareSpecials()));
	        		  BubbleManager.add(bubbles);

	        		  bubbles.clear();
		    		  bubbles.addAll((fsQuery.getFoursquareNearbyData()));
	        		  BubbleManager.add(bubbles);	        		  

	        		  Log.d("Download Manager: ", "send all fresh Foursquare objects to BubbleManager");
	        	  }
                  else{
                	  if(needLogin){
                  		Intent listIntent = new Intent(getBaseContext(), com.social.bubbles.Main.class);
                	  	getBaseContext().startActivity(listIntent);
                	  }
                	  //return to login screen
                  }
	    	  }
	    	  
	    	  if(fbQuery!=null && BubbleManager.DISPLAY_FACEBOOK_BUBBLES){
	        	  if(Session.restore(getBaseContext())!=null){
	        		  //retrieve all facebook objects
		    		  Log.d("Download Manager: ", "retrieve all relevant Facebook objects");
	        		   
		    		  LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
		    		  bubbles.addAll((fbQuery.getFacebookMyEvents()));
	        		  BubbleManager.add(bubbles);
	        		  
	        		  bubbles.clear();
		    		  bubbles.addAll((fbQuery.getFacebookRecentCheckins()));
	        		  BubbleManager.add(bubbles);

	        		  bubbles.clear();
		    		  bubbles.addAll((fbQuery.getFacebookNearbyData()));
	        		  BubbleManager.add(bubbles);

	        		  Log.d("Download Manager: ", "send all fresh Facebook objects to BubbleManager");
	        	  }
                  else{
                	  needLogin = true;
                	  //Toast.makeText(mService, "Please Login to Facebook", Toast.LENGTH_LONG).show();
                	  //return to login screen
                  }
	    	  }

    		  //BubbleManager.add(freshBubbleData);
  
//		          Message updateMessage = Message.obtain();
//		          updateMessage.what = 1;
//		          updateMessage.obj = BubbleManager.getSortedActiveList();
//		          updateMessage.setTarget(updateHandler);
//		          updateMessage.sendToTarget();
//		        	Log.d("           SENT MESSAGE TO UPDATE UI : FIRST IS ", bubbleManager.getSortedActiveList().getFirst().title);
//		
		    	  //
		    	  //FOR TESTING PURPOSES ONLY
		    	  //
		    	  Collection<Bubble> alist = BubbleManager.getSortedFreshList();
	    		  Log.d("Download Manager: Displaying Fresh List of size : ", String.valueOf(alist.size()));

		    	  for(Bubble b : alist){
		    		  Log.d("Description: ", " "+b.getDescription());

		    		  Log.d("			ID: ", " "+b.id);
		    		  Log.d("			Type: ", " "+b.getType());
		    		  Log.d("			Rank: ", " "+Long.toString(b.rank));
		    		  Log.d("			Picture: ", " "+b.getImageUrl());
		    		  Log.d("			Last Activity: ", " "+b.getLastActivity().toString());
		    	  }
		    	  Collection<Bubble> blist = BubbleManager.getSortedCacheList();
	    		  Log.d("Download Manager: Displaying Cache List of size : ", String.valueOf(blist.size()));

		    	  for(Bubble b : blist){
		    		  Log.d("Description: ", " "+b.getDescription());

		    		  Log.d("			ID: ", " "+b.id);
		    		  Log.d("			Type: ", " "+b.getType());
		    		  Log.d("			Rank: ", " "+Long.toString(b.rank));
		    		  Log.d("			Picture: ", " "+b.getImageUrl());
		    		  Log.d("			Last Activity: ", " "+b.getLastActivity().toString());
		    	  }  			        	  
					      
	      	}
	}
	    	  
    private class DownloadSignalHandler extends Handler {
        private static final int LOC_UPDATE = 0;
		private static final int BUBBLE_UPDATE = 1;

		public void handleMessage(Message msg) {
                switch (msg.what) {
                case LOC_UPDATE:
                	forceDownloadStart();
				case BUBBLE_UPDATE:
                }  
			
		}
    }
    
    public void forceDownloadStart(){
        if(!fetchInfoThread.isAlive()){
        	fetchInfoThread  = new FetchInfoThread();
        	fetchInfoThread.start();
        }
    }

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Bubble Downloader Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");

	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Bubble Downloader Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");

	}
	
	public static void setFacebookQuery(FacebookQuery fbQ){
		fbQuery = fbQ;
	}
	public static void setFoursquareQuery(FoursquareQuery fsQ){
		fsQuery = fsQ;
	}
	
	public static void setHandler(Handler updateHandler){
		DownloadManager.updateHandler = updateHandler;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}