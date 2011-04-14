package com.social.bubbles.fbquery;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bubbles.location.MyLocation;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.gson.*;
import com.social.bubbles.Bubble;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.Utils;
import com.social.bubbles.facebook.AsyncRequestListener;
import com.social.bubbles.facebook.Session;
//testing purposes
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
/**
 * 
 * @author geoffrey
 * operations supported
 * - getting all Events that take place from [events_since] to [events_until]
 * - get all Friends with [search_radius]
 * - get all Trending Places (with >[trending_qualifier] friends)
 */
public class FacebookQuery{
	//Fetching parameters
		//Nearby Places
		private final String PLACE_FIELDS = "id, name, category, location, picture, checkins";
		private final String SEARCH_RADIUS = "1000"; //METERS
		private final String PLACES_TO_CHECK = "15"; //PLACES
		private final int TRENDING_QUALIFIER = 3; //FRIENDS
		private final String NEARBY_CHECKINS_SINCE= "-18 hours";
		private final int RECENT_CHECKINS_RANK_OFFSET= 24; //hours
		private final int FRIENDS_EVENTS_RANK_OFFSET= 3; //hours

		
		//Recent Friend Checkins
		private final String RECENT_CHECKINS_SINCE = "-1 days";
		private final String RECENT_CHECKINS_LIMIT = "10";

		
		//Events
		private final String EVENTS_SINCE = "now";
		private final String EVENTS_UNTIL = "+1 week";
		
		//Friends Events
		private final String FRIENDS_EVENTS_SINCE = "now";
		private final String FRIENDS_EVENTS_UNTIL = "+2 days";

		//Users
		private final String USER_FIELDS = "id,name,picture,gender,location,events";
		
		//Pictures
		private final String PICTURE_SIZE = "normal";



	
	//private Facebook fb;
	private Activity mActivity;
	private Context mContext;
	public Context getmContext() {
		return mContext;
	}


	public Session getSession() {
		return session;
	}

	private Session session;
	private Facebook fb;
	


	//temp
    private String toastText;
    private String toastType;


	
	public FacebookQuery(Activity mActivity, Context mContext){
		this.mActivity = mActivity;
		this.mContext = mContext;
		refreshFb();
	}
	
	
	//Global Linked Lists
	private FacebookCheckinData checkinsData;
	private LinkedList<FacebookCheckin> checkins = new LinkedList<FacebookCheckin>();
	private LinkedList<FacebookPlace> trendingPlaces = new LinkedList<FacebookPlace>();
	private LinkedList<FacebookObject> fbObjects = new LinkedList<FacebookObject>();

	public LinkedList<FacebookObject> getFacebookMyEvents(){
		refreshFb();
		if(fb == null){
			Log.d("Facebook Query: ", "getReleventFacebookObjects" + " - Session is invalid.");
			return new LinkedList<FacebookObject>();
		}
		//create new linked list to hold FacebookObjects to return
		fbObjects = new LinkedList<FacebookObject>();
		
		Log.d("Facebook Query: ", "Begin My Event Parse");
		FacebookEvent[] events = getEvents().getData();
		if(events!=null){
			for(FacebookEvent anEvent : events){		//all my events & set time to start time
				anEvent.setLastActivity(Utils.timeToDate(anEvent.getStart_time()));
				anEvent.setType(Bubble.FB_MY_EVENT);
				anEvent.setDescription(anEvent.getName() + " @ " + anEvent.getLocation());
				fbObjects.add(anEvent);
			}
		}
		return resizePictures(fbObjects);
	}
	public LinkedList<FacebookObject> getFacebookRecentCheckins(){
		refreshFb();
		if(fb == null){
			Log.d("Facebook Query: ", "getReleventFacebookObjects" + " - Session is invalid.");
			return new LinkedList<FacebookObject>();
		}
		//create new linked list to hold FacebookObjects to return
		fbObjects = new LinkedList<FacebookObject>();
		
		LinkedList<FacebookUser> recentFbUsers = convertCheckinsToUsers(getRecentFriendCheckins().getData());
		Log.d("Facebook Query: ", "Number of Recent FB Checkins=" + recentFbUsers.size());
		
		Log.d("Facebook Query: ", "Begin Recent Checkin Parse");
		for(FacebookUser aUser : recentFbUsers){			//all recentFbUsers & set time to last checkin - NEARBY_CHECKINS_RANK_OFFSET hours
			Date actual = Utils.timeToDate(aUser.getCurrentCheckin().getCreated_time());
			aUser.setLastActivity(new Date((actual.getTime() - 1000*60*60*RECENT_CHECKINS_RANK_OFFSET)));
			aUser.setType(Bubble.FB_RECENT_CHECKIN);
			aUser.setImageUrl(aUser.getPicture());
			aUser.setDescription(aUser.getName() + " @ " + aUser.getCurrentCheckin().getPlace().getName());
			if(aUser.getLocation()!=null)
				aUser.setCoordinates(aUser.getLocation().getLatitude(), aUser.getLocation().getLongitude());

			fbObjects.add(aUser);
		}
		return resizePictures(fbObjects);
	}

	public LinkedList<FacebookObject> getFacebookNearbyData(){
		refreshFb();
		if(fb == null){
			Log.d("Facebook Query: ", "getReleventFacebookObjects" + " - Session is invalid.");
			return new LinkedList<FacebookObject>();
		}
		//create new linked list to hold FacebookObjects to return
		fbObjects = new LinkedList<FacebookObject>();
		
		LinkedList<FacebookUser> nearbyFbUsers = convertCheckinsToUsers(getCheckinsAroundUser().getData());
		Log.d("Facebook Query: ", "Number of Nearby FB Users=" + nearbyFbUsers.size());
		
		Log.d("Facebook Query: ", "Begin Nearby User Parse");
		for(FacebookUser aUser : nearbyFbUsers){			//all nearbyFbUsers & set time to last checkin
			aUser.setLastActivity(Utils.timeToDate(aUser.getCurrentCheckin().getCreated_time()));
			aUser.setType(Bubble.FB_NEARBY_CHECKIN);
			aUser.setImageUrl(aUser.getPicture());
			aUser.setDescription(aUser.getName() + " @ " + aUser.getCurrentCheckin().getPlace().getName());
			if(aUser.getLocation()!=null)
				aUser.setCoordinates(aUser.getLocation().getLatitude(), aUser.getLocation().getLongitude());
			

			fbObjects.add(aUser);
			
			if(aUser.getFacebookEventData() != null){
				FacebookEvent[] friendsEvents = aUser.getFacebookEventData().getData();

				if(friendsEvents!=null){
					Log.d("Facebook Query: ", "Begin Nearby Event Parse");
					for(FacebookEvent anEvent : friendsEvents){		//all friends' events & set time to start time - OFFSET
						Date actual = Utils.timeToDate(anEvent.getStart_time());
						anEvent.setLastActivity(new Date((actual.getTime() - 1000*60*60*FRIENDS_EVENTS_RANK_OFFSET)));
						anEvent.setMine(false);
						anEvent.setType(Bubble.FB_FRIENDS_EVENT);
						
						Bundle params = new Bundle();
				        params.putString("fields", "picture");
//				        params.putString("type", PICTURE_SIZE);

				        String result = "n/a";
						try {
							result = fb.request(anEvent.getId(), params);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	  
						toastText=result;
						Log.d("Facebook Query", "from getEvents(picture): " + result);
						
						FacebookObject picData= new Gson().fromJson(result, FacebookObject.class);
						
						if(picData!=null)
							anEvent.setImageUrl(picData.getPicture());
						if(anEvent.getLocation()!=null)
							anEvent.setDescription(anEvent.getName() + " @ " + anEvent.getLocation());

						fbObjects.add(anEvent);
						}
				}
			}
			else{
				Log.d("FacebookQuery Friends Events null", "");
			}
			
			
		}

		Log.d("Facebook Query: ", "Begin Trending Place Parse");
		for(FacebookPlace aPlace : trendingPlaces){	//all trending places & set time to last checkin
			//aPlace.setLastActivity(BubbleManager.timeToDate(checkinsTemp.getData()[0].getCreated_time()));
			aPlace.setType(Bubble.FB_TRENDING_PLACE);
			aPlace.setImageUrl(aPlace.getPicture());
			aPlace.setDescription(" @" + aPlace.getName() +   " has "+ aPlace.getCheckins() + " checkins.");
			if(aPlace.getLocation()!=null)
				aPlace.setCoordinates(aPlace.getLocation().getLatitude(), aPlace.getLocation().getLongitude());

			fbObjects.add(aPlace);
		}
		
		return resizePictures(fbObjects);
	}

	private LinkedList<FacebookObject> resizePictures(LinkedList<FacebookObject> list){
		LinkedList<FacebookObject> fbObjects = list;
		for(FacebookObject a : fbObjects){	//adjust all picture sizes
			String newEnding;
			if(PICTURE_SIZE.equalsIgnoreCase("normal")){
				newEnding="s.jpg";
			}
			else if(PICTURE_SIZE.equalsIgnoreCase("large")){
				newEnding="n.jpg";
			}
			else{
				newEnding="q.jpg";
			}
			a.setImageUrl(a.getImageUrl().replace("q.jpg", newEnding));
			Log.d("FacebookQuery : Object Type and picture URL ", a.getType() + " " + a.getImageUrl());
		}
		return fbObjects;
	}

	
	
	private void sendToManager(FacebookObject objectToSend){
		
	}
	
	private LinkedList<FacebookUser> convertCheckinsToUsers(FacebookCheckin[] checkins){
		LinkedList<FacebookCheckin> fbCheckins = new LinkedList<FacebookCheckin>();
		LinkedList<FacebookUser> fbUsers = new LinkedList<FacebookUser>();		
		
		//convert checkins and their tags to users
		for(FacebookCheckin aCheckin : checkins){
			FacebookUser aUser = new FacebookUser();
			aUser = aCheckin.getFrom();
			aUser.setCurrentCheckin(aCheckin);

			//Check if user is existing
			for(FacebookUser existingUser : fbUsers){
				//if bUser existing
				if(aUser.getId().equalsIgnoreCase(existingUser.getId()) && existingUser.getCurrentCheckin()!=null){
					if(aCheckin.getCreated_time().compareTo(existingUser.getCurrentCheckin().getCreated_time()) < 0){
						aUser = existingUser;
					}
				}
			}
			//if never downloaded
			if(aUser.getGender()==null){
				FacebookCheckin l = aUser.getCurrentCheckin();
				aUser=getUser(aUser.getId());
				if(l!=null){
					aUser.setCurrentCheckin(l);
				}
			}
			//existing or not existing
			fbUsers.add(aUser);		
			
			//tags
			if(aCheckin.getTags()!=null){
				for(FacebookUser aTag : aCheckin.getTags().getData()){
					aTag = getUser(aTag.getId());
					if(aTag!=null){
						aTag.setCurrentCheckin(aCheckin);
						fbUsers.add(aTag);
					}
				}
			}
		}
		
		return fbUsers;
	}

	
	private FacebookUser getUser(String id){
        String result = "n/a";
		try {
	        Bundle params = new Bundle();
	        params.putString("fields", USER_FIELDS);
	        params.putString("since", FRIENDS_EVENTS_SINCE);
	        params.putString("until", FRIENDS_EVENTS_UNTIL);


	        //params.putString("type", PICTURE_SIZE);
	        

			result = fb.request(id, params);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        toastText = result;
		toastType = "Complete User";
    	Log.d("Facebook Query", "from getUser: " + toastText);

		return new Gson().fromJson(result,FacebookUser.class);
	}
	
	private LinkedList<FacebookPlace> getPlacesAroundUser(){
		
		/**
		 * insert query
		 * jStr = JSON data fetched
		 */
        Bundle params = new Bundle();
        params.putString("type", "place");
        params.putString("limit", PLACES_TO_CHECK);
        params.putString("center", Double.toString(MyLocation.getLat()) + "," + Double.toString(MyLocation.getLon()));
        params.putString("distance", SEARCH_RADIUS);
        params.putString("fields", PLACE_FIELDS);

        
        String result = "n/a";
		try {
			result = fb.request("search", params);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}                    	

        toastText = result;
		toastType = "Places Around";
		Log.d("Facebook Query", "from getPlacesAroundUser: " + toastText);

                
        FacebookPlaceData fPlaceData =  new Gson().fromJson(result,FacebookPlaceData.class);
		
		LinkedList<FacebookPlace> placesAroundUser = new LinkedList<FacebookPlace>();
		
		placesAroundUser.addAll(Arrays.asList(fPlaceData.getData()));	
        
		return placesAroundUser;
	}
	

	
	private FacebookCheckinData getCheckinsAtPlace(String id){
		/**
		 * insert query
		 */
        Bundle params = new Bundle();
        params.putString("since", NEARBY_CHECKINS_SINCE);
        String result = "n/a";
		try {
			result = fb.request(id+"/checkins", params);
	        Log.d("Facebook Query", "from getCheckinsAtPlace: " + result.toString());
			return new Gson().fromJson(result,FacebookCheckinData.class);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new FacebookCheckinData();
	}
	
	private FacebookCheckinData getRecentFriendCheckins(){
		checkinsData = new FacebookCheckinData();
		checkins = new LinkedList<FacebookCheckin>();
		
        Bundle params = new Bundle();
        params.putString("type", "checkin");
        params.putString("since", RECENT_CHECKINS_SINCE);
        params.putString("limit", RECENT_CHECKINS_LIMIT);
        
        String result = "n/a";
		try {
			result = fb.request("search", params);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		toastText=result;
    	Log.d("Facebook Query", "from getRecentFriendCheckins: " + toastText);

        return new Gson().fromJson(result,FacebookCheckinData.class);
	}
	
	private FacebookCheckinData getCheckinsAroundUser(){
		checkinsData = new FacebookCheckinData();
		checkins = new LinkedList<FacebookCheckin>();
		
		LinkedList<FacebookPlace> places = getPlacesAroundUser();
		//get return result
		
		if(places!=null)
			Log.d("Facebook Query: ", "number of nearby places: " + places.size());
		else
			Log.d("Facebook Query: ", "No nearby places:");

		FacebookCheckinData checkinsTemp;
		
		if(places!=null){
			for (FacebookPlace aPlace : places){
		    	Log.d("Facebook Query", "Total Checkins here: " + (aPlace.getCheckins()));
		
				if((aPlace.getCheckins()) > 0){
					//for each place, get all the checkins and add to the LinkedList of checkins
					checkinsTemp = getCheckinsAtPlace(aPlace.getId());
					//identify hot place
					try{
						if(checkinsTemp.getData()!=null){
							int numCurrentCheckins = checkinsTemp.getData().length;
							aPlace.setCurrentCheckins(numCurrentCheckins);
					    	Log.d("Facebook Query", "Current Checkins here: " + numCurrentCheckins);
							if(numCurrentCheckins > TRENDING_QUALIFIER){
								aPlace.setLastActivity(Utils.timeToDate(checkinsTemp.getData()[0].getCreated_time()));
								trendingPlaces.add(aPlace);
							}
						}
			        	checkins.addAll(Arrays.asList(checkinsTemp.getData()));
					}catch(NullPointerException e){
						e.printStackTrace();
					}
				}
			}
		}
		checkinsData.setData(checkins);
		return checkinsData;
	}
	

	private FacebookEventData getEvents(){
		/**
		 * insert query
		 */
		FacebookEventData eventData;
		 Bundle params = new Bundle();
	        params.putString("since", EVENTS_SINCE);
	        params.putString("until", EVENTS_UNTIL); //day?
	        String result = "n/a";
			try {
				result = fb.request("me/events", params);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new FacebookEventData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new FacebookEventData();
			}	                    	
			toastText=result;
			Log.d("Facebook Query", "from getEvents: " + result);
//			if(result == null)
//				return new FacebookEventData();
//			else
				eventData= new Gson().fromJson(result, FacebookEventData.class);

			LinkedList<FacebookEvent> eventList = new LinkedList<FacebookEvent>();
			for(FacebookEvent event:eventData.getData()){
			params = new Bundle();
		        params.putString("fields", "picture");
//		        params.putString("type", PICTURE_SIZE);

		        result = "n/a";
				try {
					result = fb.request(event.getId(), params);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new FacebookEventData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new FacebookEventData();
				}	  
				toastText=result;
				Log.d("Facebook Query", "from getEvents(picture): " + result);
				
				FacebookObject picData= new Gson().fromJson(result, FacebookObject.class);
				event.setImageUrl(picData.getPicture());
				event.setMine(true);
				eventList.add(event);
			}
			FacebookEventData returnData = new FacebookEventData();
			returnData.setData(eventList);
			return (returnData);
	}

	public void refreshFb() {
		session = Session.restore(mContext);
		if(session!=null)
			fb = session.getFb();
		else 
			fb = null;
	}
	
	private void showToast(){
    	// display location data
        mActivity.runOnUiThread(new Runnable() {
            public void run() { 
              Toast.makeText(mContext, "Received *" + toastType + "* data:" + toastText, Toast.LENGTH_LONG)
              	.show();
            }
        });
	}
}
