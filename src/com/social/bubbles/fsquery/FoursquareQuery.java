package com.social.bubbles.fsquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedList;

import com.bubbles.location.MyLocation;
import com.frublin.androidoauth2.AndroidOAuth;
import com.google.gson.Gson;
import com.social.bubbles.Bubble;
import com.social.bubbles.Utils;
import com.social.bubbles.fbquery.FacebookEvent;
import com.social.bubbles.fbquery.FacebookObject;
import com.social.bubbles.fbquery.FacebookPlace;
import com.social.bubbles.fbquery.FacebookUser;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareCategory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author geoffrey
 * operations supported
 * - get todos of people in a wide radius
 * - get trending venues around user
 * - get people checked into places around user
 * - get specials around user
 */
public class FoursquareQuery {
	private static final String PICTURE_SIZE = "normal";
	private AndroidOAuth fs;
	private Context context;
	private FoursquareVenueSearch venueSearch;
	private FoursquareFriendSearch friendSearch;
	private FoursquareSpecialsSearch specialsSearch;
	
	public FoursquareQuery(Context context, AndroidOAuth fs){
		this.fs = fs;
		this.context = context;
		FoursquareSearch.initAndroidOAuth(fs);
	}
	
	public LinkedList<FoursquareObject> getFoursquareNearbyData(){
		venueSearch = new FoursquareVenueSearch(context, MyLocation.getLat(),MyLocation.getLon());

		LinkedList<FoursquareUser> usersAroundUser = venueSearch.getUsersAroundUser();
		LinkedList<FoursquareVenue> trendingVenuesAroundUser = venueSearch.getTrendingVenuesAroundUser();

		LinkedList<FoursquareObject> fsObjects = new LinkedList<FoursquareObject>();

		
		for(FoursquareUser aUser : usersAroundUser){			//all nearbyFsUsers & set time to last checkin
			aUser.setType(Bubble.FS_NEARBY_CHECKIN);
			aUser.setImageUrl(aUser.getPhoto());
			aUser.setDescription(aUser.getFullName() + " @ " + aUser.getCurrentCheckin().getName());
			aUser.setCoordinates(aUser.getCurrentCheckin().getLatitude(), aUser.getCurrentCheckin().getLongitude());
			
			fsObjects.add(aUser);
		}
		fsObjects=resizePictures(fsObjects);
		for(FoursquareVenue aVenue : trendingVenuesAroundUser){			//all trending places
			aVenue.setType(Bubble.FS_TRENDING_VENUE);
			aVenue.setImageUrl(aVenue.getMainCategory().icon);
			aVenue.setDescription(aVenue.getName() + " w/ " + aVenue.hereNow.count + " here now.");
			aVenue.setCoordinates(aVenue.getLatitude(), aVenue.getLongitude());

			fsObjects.add(aVenue);
		}
		
		return (fsObjects);
	}
	
	public LinkedList<FoursquareObject> getFoursquareRecentData(){
		LinkedList<FoursquareObject> fsObjects = new LinkedList<FoursquareObject>();
		FoursquareRecentSearch recentSearch = new FoursquareRecentSearch(context);
		
		LinkedList<FoursquareUser> recentCheckins = recentSearch.getRecents();
		
		for(FoursquareUser aUser : recentCheckins){			//all nearbyFsUsers & set time to last checkin
			aUser.setType(Bubble.FS_RECENT_CHECKIN);
			aUser.setImageUrl(aUser.getPhoto());
			aUser.setDescription(aUser.getFullName() + " @ " + aUser.getCurrentCheckin().getName());
			aUser.setCoordinates(aUser.getCurrentCheckin().getLatitude(), aUser.getCurrentCheckin().getLongitude());

			fsObjects.add(aUser);
		}
		return resizePictures(fsObjects);
	}

	
	public LinkedList<FoursquareObject> getFoursquareSpecials(){
		specialsSearch = new FoursquareSpecialsSearch(context, MyLocation.getLat(), MyLocation.getLon());

		LinkedList<FoursquareObject> fsObjects = new LinkedList<FoursquareObject>();
		LinkedList<FoursquareSpecial> specials = specialsSearch.getSpecials();
		for(FoursquareSpecial aSpecial : specials){			//all specials
			aSpecial.setType(Bubble.FS_SPECIAL);
			aSpecial.setImageUrl(aSpecial.getVenue().getMainCategory().icon);
			aSpecial.setDescription("@" + aSpecial.getVenue().getName() + ": " + aSpecial.getMessage().substring(0, Math.min(45, aSpecial.getMessage().length())) );
			aSpecial.setCoordinates(aSpecial.getVenue().getLatitude(), aSpecial.getVenue().getLongitude());

			//if(aSpecial.getSpecialType().equals("mayor") && I am not mayor)
			fsObjects.add(aSpecial);
		}
		return (fsObjects);
	}
	public LinkedList<FoursquareObject> getFoursquareTodos(){
		LinkedList<FoursquareObject> fsObjects = new LinkedList<FoursquareObject>();
		friendSearch = new FoursquareFriendSearch(context);
		LinkedList<FoursquareTodo> todosOfFriends = friendSearch.getTodosOfFriends();

		
		for(FoursquareTodo aTodo : todosOfFriends){			//all friend Todos
			if(!aTodo.isDone()){
				aTodo.setType(Bubble.FS_TODO);
				aTodo.setImageUrl(aTodo.getVenue().getMainCategory().icon);
				aTodo.setDescription(aTodo.getUser().getFullName() + ": Todo @ " + aTodo.getVenue().getName());
				aTodo.setCoordinates(aTodo.getVenue().getLatitude(), aTodo.getVenue().getLongitude());
	
				Log.d("FoursquareQuery : Todo Last Activity: ", ""+aTodo.getLastActivity().toString());

				fsObjects.add(aTodo);
			}
		}
		return (fsObjects);
	}
	
	
	private LinkedList<FoursquareObject> resizePictures(LinkedList<FoursquareObject> list){
		LinkedList<FoursquareObject> fsObjects = list;
		for(FoursquareObject a : fsObjects){	//adjust all picture sizes
			String newEnding;
			if(PICTURE_SIZE.equalsIgnoreCase("normal")){
			}
			else if(PICTURE_SIZE.equalsIgnoreCase("large")){
				a.setImageUrl(a.getImageUrl().replace("_thumbs", ""));
			}
			else{
			}
			Log.d("FoursquareQuery : Object Type and picture URL ", a.getType() + " " + a.getImageUrl());
		}
		return fsObjects;
	}
	
	public boolean isSessionValid(){
		return fs.isSessionValid();
	}
}
