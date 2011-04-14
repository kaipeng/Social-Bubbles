package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;

/**
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
**/

import com.social.bubbles.Bubble;

import com.google.gson.Gson;

public class FoursquareSpecialsSearch extends FoursquareSearch{
	private FoursquareResponse response;
	
	/**
	public FoursquareSpecialsSearch(Context context, double latitude, double longitude){
		super(context);
		
		String query = "specials/search?ll="+latitude+","+longitude;
		Bundle params = new Bundle();
		params.putString("ll", latitude+","+longitude);
		query = this.queryFoursquare(query, params);
		if (query!=""){
			response = new Gson().fromJson(query, FoursquareSpecialsSearch.class).response;
		}else {
			response = null;
		}
		Log.d("FoursquareQuery : FoursquareSpecialsSearch Result ", query);
	}
	**/
	
	public LinkedList<FoursquareSpecial> getSpecials(){
		if (response==null){
			return new LinkedList<FoursquareSpecial>();
		}
		return new LinkedList<FoursquareSpecial>(Arrays.asList(response.specials.items));
	}
	
	public LinkedList<Bubble.SubBubble> getSubBubbles(){
		LinkedList<Bubble.SubBubble> subBubbles = new LinkedList<Bubble.SubBubble>();
		subBubbles.add(new AddToTodosSubBubble());
		return subBubbles;
	}
	

	public static class FoursquareResponse {
		private FoursquareSpecialsList specials; 
		public static class FoursquareSpecialsList {
			private int count;
			private FoursquareSpecial[] items;
		}
	}
	//sub bubbles
	private class AddToTodosSubBubble implements Bubble.SubBubble {

		@Override
		public void getImageUrl() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
