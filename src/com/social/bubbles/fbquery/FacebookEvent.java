package com.social.bubbles.fbquery;

import java.util.Date;

import com.social.bubbles.BubbleManager;
import com.social.bubbles.Utils;


public class FacebookEvent extends FacebookObject {
	private String name;
	private String start_time;
	private String end_time;
	private String location;
	private String rsvp_status;
		
	//setters
	public void setName(String name){
		this.name = name;
	}
	public void setStart_time(String start_time){
		this.start_time = start_time;
	}
	public void setEnd_time(String end_time){
		this.end_time = end_time;
	}
	public void setLocation(String location){
		this.location = location;
	}
	public void setRsvp_status(String rsvp_status){
		this.rsvp_status = rsvp_status;
	}
	//getters
	public String getName(){
		return name;
	}
	public String getStart_time(){
		return start_time;
	}
	public Date getEndTime(){
		return Utils.timeToDate(end_time);
	}
	public String getLocation(){
		return location;
	}
	public String getRsvp_status(){
		return rsvp_status;
	}
	public void setMine(boolean isMine) {
		super.isMine = isMine;
	}
	public boolean isMine() {
		return isMine;
	}
	
}
