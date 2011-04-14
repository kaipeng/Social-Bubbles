package com.social.bubbles;

import java.util.Date;
import java.util.LinkedList;

public class Bubble {
	protected String id;
	private String imageUrl;
	private Date timeUpdated;
	
	//setters
	public void updateTime(){
		timeUpdated = new Date();
	}
	
	//getters
	public String getId(){
		return id;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	public LinkedList<SubBubble> getSubBubbles(){
		return new LinkedList<SubBubble>();
	}
	public Date getTimeLastUpdated(){
		return timeUpdated;
	}
	
	
	
	
	
	public interface SubBubble {
		public void getImageUrl();
		public void onClick();
	}
}
