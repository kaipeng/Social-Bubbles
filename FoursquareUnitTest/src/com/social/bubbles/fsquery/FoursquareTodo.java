package com.social.bubbles.fsquery;

public class FoursquareTodo extends FoursquareObject {
	private FoursquareTip tip;
	
	public FoursquareVenue getVenue(){
		return tip.venue;
	}
	
	public String getText(){
		return tip.text;
	}
	
	public String getStatus(){
		return tip.status;
	}
	
	public void setUser(FoursquareUser user) {
		tip.user = user;
	}
	public FoursquareUser getUser() {
		return tip.user;
	}
	public boolean isDone(){
		return (tip.status).equalsIgnoreCase("done");
	}
	public static  class FoursquareTip extends FoursquareObject {
		private String text;
		private String status;
		private FoursquareVenue venue;
		private FoursquareUser user;

		
	}
}
