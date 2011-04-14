package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.social.bubbles.Bubble;

import com.google.gson.Gson;

public class FoursquareSpecialsSearch extends FoursquareSearch{
	private FoursquareSpecialsResponse response;
	
	
	public FoursquareSpecialsSearch(Context context, double latitude, double longitude){
		super(context);
		
		String query = "specials/search";
		Bundle params = new Bundle();
		params.putString("ll", latitude+","+longitude);
		query = this.queryFoursquare(query, params);
		if (query!=""){
			response = new Gson().fromJson(query, FoursquareSpecialsResponseClass.class).response;
		}else {
			response = null;
		}
		Log.d("FoursquareQuery : FoursquareSpecialsSearch Result ", query);
	}
	
	
	public LinkedList<FoursquareSpecial> getSpecials(){
		if (response==null){
			return new LinkedList<FoursquareSpecial>();
		}
		if (response.specials==null){
			return new LinkedList<FoursquareSpecial>();
		}
		return new LinkedList<FoursquareSpecial>(Arrays.asList(response.specials.items));
	}
	
	public LinkedList<Bubble.SubBubble> getSubBubbles(){
		LinkedList<Bubble.SubBubble> subBubbles = new LinkedList<Bubble.SubBubble>();
		subBubbles.add(new AddToTodosSubBubble());
		return subBubbles;
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
