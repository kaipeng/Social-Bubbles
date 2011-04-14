package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
import android.content.Context;
import android.util.Log;
**/

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

public class FoursquareFriendSearch extends FoursquareSearch {
	private FoursquareFriendResponse response;
	
	
	public FoursquareFriendSearch(Context context){
		super(context);
		String query = "users/self/friends";
		
		query = this.queryFoursquare(query);
		
		Log.d("FoursquareQuery : FoursquareFriendsSearch Result ", query);
		if(query==null){
			query = this.queryFoursquare(query);

		}
		FoursquareFriendResponseClass a = new Gson().fromJson(query,FoursquareFriendResponseClass.class);
		if(a!= null)
			response = a.response;
	}
	
	
	public LinkedList<FoursquareUser> getFriends(){
		if(response==null)
			return new LinkedList<FoursquareUser>();
		if(response.friends == null)
			return new LinkedList<FoursquareUser>();

		return new LinkedList<FoursquareUser>(Arrays.asList(response.friends.items));
	}
	
	
	public LinkedList<FoursquareTodo> getTodosOfFriends(){
		LinkedList<FoursquareTodo> todos = new LinkedList<FoursquareTodo>();
		
		Log.d("FoursquareFriendsSearch :  Parsing through list of friends: ", ""+getFriends().size());

		for (FoursquareUser aFriend : getFriends()){
			String query = "users/" + aFriend.getId() + "/todos";
			Bundle params = new Bundle();
			params.putString("sort", "recent");
			params.putString("limit", "3");

			query = this.queryFoursquare(query, params);
			FoursquareTodoResponse response = new Gson().fromJson(query,FoursquareTodoResponseClass.class).response;
			Log.d("FoursquareQuery : FoursquareTodoSearch Result ", query);
			
			for(FoursquareTodo todo: Arrays.asList(response.getTodos())){
				todo.setLastActivity(new Date(todo.getCreatedAt()));
				Log.d("FoursquareTodoSearch : Todo Created at: ", ""+todo.getCreatedAt());
				Log.d("FoursquareTodoSearch : Todo Last Activity: ", ""+todo.getLastActivity().toString());

				todo.setUser(aFriend);
				todos.add(todo);
			}
		}
		return todos;
	}
	
	
}
