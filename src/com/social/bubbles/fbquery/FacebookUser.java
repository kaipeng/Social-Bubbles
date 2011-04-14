package com.social.bubbles.fbquery;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;


public class FacebookUser extends FacebookObject {
	private String name;
	private String gender;
	private FacebookLocation location;

	
	//supplementary
	private FacebookEventData events;
	//private FacebookCheckinData facebookCheckinData;
	
	private FacebookCheckin currentCheckin = null;
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	
	public String getName(){
		return name;
	}
	
	public String getGender(){
		return gender;
	}

//	public void setFacebookEventData(FacebookEventData facebookEventData) {
//		this.facebookEventData = facebookEventData;
//	}
//
	public FacebookEventData getFacebookEventData() {
		return events;
	}
//
//	public void setFacebookCheckinData(FacebookCheckinData facebookCheckinData) {
//		this.facebookCheckinData = facebookCheckinData;
//	}
//	public FacebookCheckinData getFacebookCheckinData() {
//		return facebookCheckinData;
//	}
	public void setCurrentCheckin(FacebookCheckin currentCheckin){
		this.currentCheckin = currentCheckin;
	}
	public FacebookCheckin getCurrentCheckin() {
		return currentCheckin;
	}

	public void setLocation(FacebookLocation location) {
		this.location = location;
	}

	public FacebookLocation getLocation() {
		return location;
	}


}
