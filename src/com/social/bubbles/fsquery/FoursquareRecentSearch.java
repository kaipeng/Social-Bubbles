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
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNow.FoursquareCheckin;

public class FoursquareRecentSearch extends FoursquareSearch {
	private FoursquareRecentResponse response;
	
	
	public FoursquareRecentSearch(Context context){
		super(context);
		String query = "checkins/recent";
		Bundle params = new Bundle();
		params.putString("limit", "7");
		query = this.queryFoursquare(query, params);
		
		response = new Gson().fromJson(query,FoursquareRecentResponseClass.class).response;
		
		Log.d("FoursquareQuery : FoursquareRecentsSearch Result ", query);
	}
	
	
	public LinkedList<FoursquareUser> getRecents(){
		if(response!=null){
			LinkedList<FoursquareCheckin> checkins = new LinkedList<FoursquareCheckin>(Arrays.asList(response.recent));
			LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
			for(FoursquareCheckin aCheckin: checkins){
				FoursquareUser aUser = aCheckin.user;
				aUser.setCurrentCheckin(aCheckin.venue);
				aUser.setLastActivity(new Date(aCheckin.getCreatedAt() - 1000*60*20));
				//20 minute recent offset

				users.add(aUser);
			}
			return users;
		}
		return new LinkedList<FoursquareUser>();
	}
	
	
	public LinkedList<FoursquareTodo> getTodosOfRecents(){
		LinkedList<FoursquareTodo> todos = new LinkedList<FoursquareTodo>();
		
		Log.d("FoursquareRecentsSearch :  Parsing through list of Recents: ", ""+getRecents().size());

		for (FoursquareUser aRecent : getRecents()){
			String query = "users/" + aRecent.getId() + "/todos";
			Bundle params = new Bundle();
			params.putString("sort", "recent");
			params.putString("limit", "3");

			query = this.queryFoursquare(query, params);
			FoursquareTodoResponse response = new Gson().fromJson(query,FoursquareTodoResponseClass.class).response;
			Log.d("FoursquareQuery : FoursquareTodoSearch Result ", query);
			
			for(FoursquareTodo todo: Arrays.asList(response.getTodos())){
				todos.add(todo);
			}
		}
		return todos;
	}
	
	
}
