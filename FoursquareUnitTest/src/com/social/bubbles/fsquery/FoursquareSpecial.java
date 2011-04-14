package com.social.bubbles.fsquery;

public class FoursquareSpecial extends FoursquareObject {
	private String type;
	private String message;
	private String description;
	private boolean unlocked;
	private FoursquareVenue venue;
	
	public String getType(){
		return type;
	}
	public String getMessage(){
		return message;
	}
	public String getDescription(){
		return description;
	}
	public boolean getUnlocked(){
		return unlocked;
	}
	public FoursquareVenue getVenue(){
		return venue;
	}
}
