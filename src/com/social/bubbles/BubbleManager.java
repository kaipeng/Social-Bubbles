package com.social.bubbles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import android.content.Context;
import android.util.Log;

public class BubbleManager{
	static HashMap<String, Bubble> freshBubbles = new HashMap<String, Bubble>();
    public static HashMap<String, Bubble> cache = new HashMap<String, Bubble>();
	static LinkedList<String> displayQueue = new LinkedList<String>();

	public static int FRESH_QUALIFIER = 30;//minutes
	public static int DISPLAY_QUEUE_SIZE = 10;
	public static int CACHE_SIZE = 100;

	//settings for features
	static boolean DISPLAY_ONLY_FRESH_BUBBLES = false;
	static boolean DISPLAY_FACEBOOK_BUBBLES = true;
		static boolean DISPLAY_FACEBOOK_EVENTS = true;
		static boolean DISPLAY_FACEBOOK_FRIENDSEVENTS = true;
		static boolean DISPLAY_FACEBOOK_RECENTCHECKINS = true;
		static boolean DISPLAY_FACEBOOK_NEARBYCHECKINS = true;
		static boolean DISPLAY_FACEBOOK_TRENDINGPLACES = true;
	static boolean DISPLAY_FOURSQUARE_BUBBLES = true;
		static boolean DISPLAY_FOURSQUARE_NEARBYCHECKINS = true;
		static boolean DISPLAY_FOURSQUARE_RECENTCHECKINS = true;
		static boolean DISPLAY_FOURSQUARE_TRENDINGPLACES = true;
		static boolean DISPLAY_FOURSQUARE_SPECIALS = true;
		static boolean DISPLAY_FOURSQUARE_TODOS = true;
		static boolean DISPLAY_FOURSQUARE_FRIENDSONLY = true;

	static BubbleLauncher bubbleLauncher;

    static File cacheDir;

    static Context mContext;
	
	//Constructor
	public BubbleManager(Context context){
		mContext = context;
		
		bubbleLauncher = new BubbleLauncher(mContext);
		
        //Find the dir to save cached data
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"SocialBubbles/BubbleData");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        //load cached bubble history
        //loadHistoryCache();//**************
        LinkedList<Bubble> sortedCache = getSortedList(cache);
        //queue some intial bubbles from the cache
        for(int i = 0; i < DISPLAY_QUEUE_SIZE &&  i < sortedCache.size(); i++){
        	Bubble bub = sortedCache.get(i);
        	freshBubbles.put(bub.getId(), bub);
        	displayQueue.add(bub.getId());
        }
        
		BubbleLauncher.RefreshBubbles();
        //just to get the updater loop running
   	}
	
	//Check if cache of bubbles list contains a key
	public static boolean contains(String id){
		return cache.containsKey(id);
	}
	
	//Add a Bubble Object  and update if ID already exists
	public static void add(Bubble aObject){
		for(Bubble fBubble : freshBubbles.values()){
			if(fBubble.getLastDownloaded().before(new Date(new Date().getTime() - 1000*60*FRESH_QUALIFIER))){
				freshBubbles.remove(fBubble.getId());
			}
		}
		  Log.d("Bubble Manager: ", "removed old bubbles more than " + String.valueOf(FRESH_QUALIFIER) + " minutes ago from Fresh List" );

		  String id = aObject.getId();
			aObject.setLastDownloaded();
			aObject.updateRank();
			//Add new bubbles to fresh list
			if(freshBubbles.containsKey(id)){
				freshBubbles.remove(id);
			}
			freshBubbles.put(id, aObject);
			Log.d("Bubble Manager: Bubble added to FreshBubbles List : ", aObject.getType() + aObject.getId());

			
			//Add new bubbles to cache list
			if(cache.containsKey(id)){
				cache.remove(id);
			}
			cache.put(id, aObject);
			Log.d("Bubble Manager: Bubble added to Cache List : ", aObject.getType() + aObject.getId());

		
		//saveHistoryCache();//**************

		//Now take this queue and create bubbles from it
		BubbleLauncher.RefreshBubbles();
		
		queueQualifyingBubbles();
	}
	
	//Add a Bubble List  and update if ID already exists
	public static void add(LinkedList<Bubble> newData){
		//remove bubbles downloaded more than [FRESH_QUALIFIER] minutes ago from Fresh list
		for(Bubble fBubble : freshBubbles.values()){
			if(fBubble.getLastDownloaded().before(new Date(new Date().getTime() - 1000*60*FRESH_QUALIFIER))){
				freshBubbles.remove(fBubble.getId());
			}
		}
		  Log.d("Bubble Manager: ", "removed old bubbles more than " + String.valueOf(FRESH_QUALIFIER) + " minutes ago from Fresh List" );

		for(Bubble aObject: newData){			
			String id = aObject.getId();
			aObject.setLastDownloaded();
			aObject.updateRank();
			//Add new bubbles to fresh list
			if(freshBubbles.containsKey(id)){
				freshBubbles.remove(id);
			}
			freshBubbles.put(id, aObject);
			Log.d("Bubble Manager: Bubble added to FreshBubbles List : ", aObject.getType() + aObject.getId());

			
			//Add new bubbles to cache list
			if(cache.containsKey(id)){
				cache.remove(id);
			}
			cache.put(id, aObject);
			Log.d("Bubble Manager: Bubble added to Cache List : ", aObject.getType() + aObject.getId());

		}
		//saveHistoryCache();//**************

		//Now take this queue and create bubbles from it
		BubbleLauncher.RefreshBubbles();
		
		queueQualifyingBubbles();
	}
	
	//add from all bubbles in order of rank until display queue is filled (bubbles desired on screen)
	// add only ones we want (setting turned on)
	// also honor desire only to show "fresh" bubbles
	// RETURNS how many were added to queue
	public static int queueQualifyingBubbles(){ 
		displayQueue.clear();
		LinkedList<Bubble> desiredList;
		
		//adds qualifying bubbles from ALL CACHED or ONLY FRESH
		if(DISPLAY_ONLY_FRESH_BUBBLES){
			desiredList = getSortedFreshList();
		}
		else{
			desiredList = getSortedCacheList();
		}
		Log.d("Bubble Manager: Desired List Size = ", String.valueOf(desiredList.size()));

		//checks for qualifying types
		for(Bubble aBub : desiredList){
			String type=aBub.getType();
			Log.d("Bubble Manager: Attempting to add Bubble Queue - ", aBub.getType() + " " + aBub.getId());

			if(type.equalsIgnoreCase(Bubble.FB_MY_EVENT) && DISPLAY_FACEBOOK_EVENTS ||
					type.equalsIgnoreCase(Bubble.FB_FRIENDS_EVENT) && DISPLAY_FACEBOOK_FRIENDSEVENTS ||
					type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN) && DISPLAY_FACEBOOK_RECENTCHECKINS ||
					type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN) && DISPLAY_FACEBOOK_NEARBYCHECKINS ||
					type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE) && DISPLAY_FACEBOOK_TRENDINGPLACES ||
					type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN) && DISPLAY_FOURSQUARE_NEARBYCHECKINS ||
					type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN) && DISPLAY_FOURSQUARE_RECENTCHECKINS ||
					type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE) && DISPLAY_FOURSQUARE_TRENDINGPLACES || 
					type.equalsIgnoreCase(Bubble.FS_SPECIAL) && DISPLAY_FOURSQUARE_SPECIALS ||
					type.equalsIgnoreCase(Bubble.FS_TODO) && DISPLAY_FOURSQUARE_TODOS){
				
					Log.d("Bubble Manager: Adding to Queue - ", aBub.getType() + " " + aBub.getId());
					displayQueue.add(aBub.getId());
					Log.d("Bubble Manager: Bubble Added to Queue - ", aBub.getType() + " " + aBub.getId());

				 
				if(displayQueue.size() >= DISPLAY_QUEUE_SIZE){
					break;
				}
			}
			else{
				Log.d("Bubble Manager: Bubble Rejected due to settigns : ", type + aBub.getId());
			}
		}
		return displayQueue.size();
	}
	//save cache to external file
	public static void saveHistoryCache(){
		Log.d("Bubble Manager: Cache Begin Size = ", String.valueOf(cache.size()));
		//If cache is too large, cut it in half
		if(cache.size() > CACHE_SIZE){
			LinkedList<Bubble> temp = getSortedList(cache);
			for(int i = cache.size(); i > CACHE_SIZE/2; i--){
				cache.remove(temp.get(i).getId());
			}
		}
		//write object to file
		try {
			Log.d("Bubble Manager: Cache Writing to:  ", cacheDir + "cache.hash");
	        File fc=new File(cacheDir, "cache.hash");
	        fc.delete();
	        File f=new File(cacheDir, "cache.hash");
	        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
	        out.writeObject(cache.values().toArray()[0]);
	        out.close();
			Log.d("Bubble Manager: Cache Written to File : Size = ", String.valueOf(cache.size()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//load cache from external file
	public static void loadHistoryCache(){
		try {
			Log.d("Bubble Manager: Cache Load from File : ", String.valueOf(cache.size()));

	        File f=new File(cacheDir, "cache.hash");
	        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
	        Object readCache;
				readCache = in.readObject();
		        if(readCache.getClass().equals(cache))
		        	cache = (HashMap<String, Bubble>) readCache;
		        in.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	//returns the active list sorted by rank algorithm
	public static LinkedList<Bubble> getSortedList(HashMap<String, Bubble> mapToSort){
		LinkedList<Bubble> a = new LinkedList<Bubble>();
		for(Bubble b : mapToSort.values()){
				b.updateRank();
				a.add(b);
		}
	
		Collections.sort(a, new Comparator<Bubble>(){
			@Override
			public int compare(Bubble object1, Bubble object2) {
				return	(int) (object1.rank - object2.rank);
			}
		});
		return a;
	}
	
	//returns the active list sorted by rank algorithm
	public static LinkedList<Bubble> getSortedCacheList(){
		return getSortedList(cache);
	}
	//returns the active list sorted by rank algorithm
	public static LinkedList<Bubble> getSortedFreshList(){
		return getSortedList(freshBubbles);
	}

	
    public void clearCache() {
        //clear memory cache
        cache.clear();
        
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }
}