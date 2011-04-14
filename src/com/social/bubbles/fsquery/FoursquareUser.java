package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;

public class FoursquareUser extends FoursquareObject {
	private String firstName;
	private String lastName;
	private String homeCity;
	private String photo;
	private String gender;
	private String relationship;
	private FoursquareContact contact;
	private FoursquareTodoList todos;
	private FoursquareVenue currentCheckin;
	
	//name getters
	public String getFirstName(){
		return firstName;
	}
	public String getLastName(){
		return lastName;
	}
	public String getFullName(){
		return (firstName + " " + lastName);
	}
	
	//contact getters
	public String getEmail(){
		return contact.email;
	}
	public String getFacebook(){
		return contact.facebook;
	}
	public String getTwitter(){
		return contact.twitter;
	}
	public String getPhone(){
		return contact.phone;
	}
	
	//FoursquareTodo getters
	public boolean hasTodos(){
		return todos.count>0;
	}
	public LinkedList<FoursquareTodo> getTodos(){
		return new LinkedList<FoursquareTodo>(Arrays.asList(todos.items));
	}

	
	

	
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPhoto() {
		return photo;
	}





	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getGender() {
		return gender;
	}





	public void setCurrentCheckin(FoursquareVenue currentCheckin) {
		this.currentCheckin = currentCheckin;
	}
	public FoursquareVenue getCurrentCheckin() {
		return currentCheckin;
	}





	public void setHomeCity(String homeCity) {
		this.homeCity = homeCity;
	}
	public String getHomeCity() {
		return homeCity;
	}





	public static class FoursquareContact extends FoursquareObject {
		public String email;
		public String facebook;
		public String twitter;
		public String phone;
	}
	public static class FoursquareTodoList extends FoursquareObject {
		public int count;
		public FoursquareTodo[] items;
	}
}
