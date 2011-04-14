package com.social.bubbles.fsquery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
**/

import com.google.gson.Gson;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNow;

/**
 * FoursquareVenueSearch retrieves a lot of data with the venue search
 * operations include:
 * - getting venues around user
 * - getting trending venues around user
 * - getting people checked into venues around user
 * @author geoffrey
 *
 */
public class FoursquareVenueSearch extends FoursquareSearch {
	private FoursquareResponse response;
	public Meta meta;
	
	public static class Meta {
		public int code;
	}
	
	public static void test() throws IOException{		
		byte[] buffer = new byte[(int) new File("venueSearchTest.txt").length()];
	    BufferedInputStream f = new BufferedInputStream(new FileInputStream("venueSearchTest.txt"));
	    f.read(buffer);
	    String query =  new String(buffer);
		FoursquareResponse response = new Gson().fromJson(query, FoursquareResponse.class);
	}
	
	/**
	 * instantiate a search with only the current latitude and longitude 
	 * @param latitude, longitude
	 */
	
	/**
	public FoursquareVenueSearch(Context context, double latitude, double longitude){
		super(context);
		String query = "venues/search";
		Bundle params = new Bundle();
		params.putString("ll",latitude+","+longitude);
		query = this.queryFoursquare(query, params);
		if (query!=""){
			response = new Gson().fromJson(query, FoursquareResponse.class);
		}
		Log.d("FoursquareQuery : FoursquareVenueSearch Result ", query);
	}
	**/
	
	
	//getters
	
	public LinkedList<FoursquareVenue> getVenuesAroundUser(){
		if (response==null){
			return new LinkedList<FoursquareVenue>();
		}
		return response.getAllVenues();
	}
	/**
	public LinkedList<FoursquareVenue> getTrendingVenuesAroundUser(){
		if (response==null){
			return new LinkedList<FoursquareVenue>();
		}
		return response.getTrendingVenues();
	}
	**/
	/**
	public LinkedList<FoursquareUser> getUsersAroundUser(){
		if (getVenuesAroundUser()==null){
			return new LinkedList<FoursquareUser>();
		}
		LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
		for (FoursquareVenue venue : getVenuesAroundUser()){
			users.addAll(getUsersAtVenue(venue));
		}
		return users;
	}
	**/

	
	
	/**
	private LinkedList<FoursquareUser> getUsersAtVenue(FoursquareVenue venue){
		if (venue==null || venue.hereNow.count == 0){
			return new LinkedList<FoursquareUser>();
		}
		String query = "venues/"+venue.getId()+"/herenow";
		String response = this.queryFoursquare(query);
		Log.d("FoursquareQuery : GetUsersAtVenue Result ", query);

		FoursquareHereNow fsHN = new Gson().fromJson(response, FoursquareHereNow.class);
		
		LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
		for(FoursquareUser aUser: fsHN.getUsers()){
			aUser.setCurrentCheckin(venue);
			users.add(aUser);
		}
		return users;
	}
	**/
	
	public static class FoursquareResponse extends FoursquareObject{
		private FoursquareGroup[] groups;
		public LinkedList<FoursquareVenue> getAllVenues(){
			LinkedList<FoursquareVenue> venues = new LinkedList<FoursquareVenue>();
			for (FoursquareGroup g : groups){
				venues.addAll(g.getVenues());
			}
			return venues;
		}
		/**
		public LinkedList<FoursquareVenue> getTrendingVenues(){
			LinkedList<FoursquareVenue> venues = new LinkedList<FoursquareVenue>();
			for (FoursquareGroup g : groups){
				if (g.name.equals("Trending Now")){
					for(FoursquareVenue v : g.getVenues()){
						v.setLastActivity(new Date());
						venues.add(v);
					}
				}
			}
			return venues;
		}
		**/
	}
	public static class FoursquareGroup extends FoursquareObject{
		private String type;
		private String name;
		private FoursquareVenue[] items;
		
		public String getType(){
			return type;
		}
		public String getName(){
			return name;
		}
		public LinkedList<FoursquareVenue> getVenues(){
			return new LinkedList<FoursquareVenue>(Arrays.asList(items));
		}
	}
}
