package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class FoursquareVenue extends FoursquareObject {
	private String name;
	private FoursquareLocation location;
	private FoursquareCategory[] categories;
	public FoursquareHereNow hereNow;
	
	public String getName(){
		return name;
	}
	
	
	//location getters
	/**
	 * example: "Adams St/Brooklyn Bridge Blvd"
	 * @return	address of venue
	 */
	public String getAddress(){
		return location.address;
	}
	public String getCity(){
		return location.city;
	}
	public String getState(){
		return location.state;
	}
	public double getLatitude(){
		return location.lat;
	}
	public double getLongitude(){
		return location.lng;
	}
	
	
	/**
	 * returns categories the venue falls under.
	 * a category is what kind of place this venue is
	 * examples "Great Outdoors," "Bridges" for "Brooklyn Bridge"
	 * 
	 * @return categories the venue falls under
	 */
	public FoursquareCategory getCategory(){
		for(FoursquareCategory c : categories){
			if(c.primary){
				return c;
			}
		}
		return null;
	}
		
	
	public static class FoursquareLocation extends FoursquareObject{
		public String address;
		public String city;
		public String state;
		public String postalCode;
		public String country;
		public double lat;
		public double lng;
		public double distance;
	}
	public static class FoursquareCategory extends FoursquareObject{
		public String name;
		public String icon;
		public String[] parents;
		public boolean primary;
	}
	public static class FoursquareHereNow extends FoursquareObject{
		public int count;
		public FoursquareCheckin[] items;
		
		/**
		public LinkedList<FoursquareUser> getUsers(){
			if (count==0){
				return new LinkedList<FoursquareUser>();
			}
			LinkedList<FoursquareUser> fsUsers = new LinkedList<FoursquareUser>();
			for(FoursquareCheckin a: items){
				FoursquareUser user = a.user;
				user.setLastActivity(new Date(a.getCreatedAt()));
				fsUsers.add(a.user);
			}
			return (fsUsers);
		}
		**/
		
		public static class FoursquareCheckin extends FoursquareObject{
			public String type;
			protected FoursquareUser user;
		}
	}
}
