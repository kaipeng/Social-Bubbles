package com.social.bubbles.fsquery;

import java.io.IOException;
import java.net.MalformedURLException;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.frublin.androidoauth2.AndroidOAuth;


public class FoursquareSearch {
	
	private static AndroidOAuth fs;
	
	public static void initAndroidOAuth(AndroidOAuth fsa){
		fs=fsa;
		Log.d("Foursquare Search: Initialized", "Token = " + fs.getSavedAccessToken());
	}
	public FoursquareSearch(Context context){
		String accessToken = fs.getSavedAccessToken(PreferenceManager.getDefaultSharedPreferences(context));
		Log.d("Foursquare Search: ", "Token = ");
		if (accessToken == null){
			fs = null;
		}
	}
	
	public String queryFoursquare(String query) {
		String result;
		if (fs==null){
			return "";
		}
		try {
			result = fs.request(query);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "";
		}
		return result;
	}
	public String queryFoursquare(String query, Bundle parameters){
		String result;
		if (fs==null){
			return "";
		}
		try {
			result = fs.request(query, parameters);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "";
		}
		return result;
	}
	
}
