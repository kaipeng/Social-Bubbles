package com.social.bubbles.fsquery;

import java.io.IOException;
import java.net.MalformedURLException;

/**
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.frublin.androidoauth2.AndroidOAuth;
**/

public class FoursquareSearch {
	/**
	private AndroidOAuth fs;
	public FoursquareSearch(Context context){
		fs = new AndroidOAuth(context);
		String accessToken = fs.getSavedAccessToken(PreferenceManager.getDefaultSharedPreferences(context));
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
	**/
}
