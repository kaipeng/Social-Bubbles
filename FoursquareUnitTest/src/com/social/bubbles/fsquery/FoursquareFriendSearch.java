package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;

/**
import android.content.Context;
import android.util.Log;
**/

import com.google.gson.Gson;

public class FoursquareFriendSearch extends FoursquareSearch {
	private FoursquareResponse response;
	
	/**
	public FoursquareFriendSearch(Context context){
		super(context);
		String query = "users/self/friends";
		
		query = this.queryFoursquare(query);
		
		response = new Gson().fromJson(query,FoursquareResponse.class);
		
		Log.d("FoursquareQuery : FoursquareSpecialsSearch Result ", query);
	}
	**/
	
	public LinkedList<FoursquareUser> getFriends(){
		return new LinkedList<FoursquareUser>(Arrays.asList(response.friends.items));
	}
	
	/**
	public LinkedList<FoursquareTodo> getTodosOfFriends(){
		LinkedList<FoursquareTodo> todos = new LinkedList<FoursquareTodo>();
		
		for (FoursquareUser aFriend : getFriends()){
			String query = "users/" + aFriend.getId() + "/todos?sort=recent";
			query = this.queryFoursquare(query);
			FoursquareTodoResponse response = new Gson().fromJson(query,FoursquareTodoResponse.class);
			Log.d("FoursquareQuery : FoursquareSpecialsSearch Result ", query);
			
			for(FoursquareTodo todo: Arrays.asList(response.getTodos())){
				todos.add(todo);
			}
		}
		return todos;
	}
	**/
	
	public static class FoursquareResponse extends FoursquareObject{
		private FoursquareFriendsList friends;
		
		public static class FoursquareFriendsList extends FoursquareObject {
			private int count;
			private FoursquareUser[] items;
		}
	}
	
	public static class FoursquareTodoResponse extends FoursquareObject{
		private FoursquareTodosList todos;
	

		public FoursquareTodo[] getTodos() {
			return todos.items;
		}
		
		public static class FoursquareTodosList extends FoursquareObject {
			protected int count;
			protected FoursquareTodo [] items;
		}
	}
}
