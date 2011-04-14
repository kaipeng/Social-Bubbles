package com.social.bubbles;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.social.bubbles.fsquery.FoursquareObject;

public class Bubble implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1671660991705117668L;
	
	protected String id;
	private String imageUrl;
	private Date lastDownloaded = new Date();
	private Date lastActivity = new Date();

	protected boolean isMine;
	private String type;
	public String description;
	
	double lat, lon;
	
	public void setCoordinates(double mlat, double mlon){
		lat = mlat;
		lon = mlon;
	}

	public double getLat(){
		return lat;
	}
	public double getLon(){
		return lon;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public final static String FB_MY_EVENT			= "fbmyevent";
	public final static String FB_FRIENDS_EVENT		= "fbfriendsevent";
	public final static String FB_NEARBY_CHECKIN	= "fbnearbycheckin";
	public final static String FB_RECENT_CHECKIN	= "fbrecentcheckin";
	public final static String FB_TRENDING_PLACE	= "fbtrendingplace";
	
	public final static String FS_NEARBY_CHECKIN	= "fsnearbycheckin";
	public final static String FS_RECENT_CHECKIN	= "fsrecentcheckin";
	public final static String FS_SPECIAL			= "fsspecial";
	public final static String FS_TODO				= "fstodo";
	public final static String FS_TRENDING_VENUE	= "fstrendingvenue";



	public long rank; //smaller the better
	
	//setters
	public void updateRank(){
		rank = Math.abs(lastActivity.getTime()-(new Date()).getTime());
	}
	
	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}
	
	public void setLastDownloaded() {
		this.lastDownloaded = new Date();
	}
	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}
	public void setType(String type) {
		this.type = type;
	}


	//getters
	public long getRank(){
		return rank;
	}
	public String getId(){
		return id;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	public LinkedList<SubBubble> getSubBubbles(){
		return subBubbles;
	}
	public Date getLastDownloaded() {
		return lastDownloaded;
	}

	public String getType() {
		return type;
	}
	
	
	public boolean equals(Bubble object){
		return this.id.equalsIgnoreCase(object.id);
	}

	LinkedList<SubBubble> subBubbles = new LinkedList<SubBubble>();
	public interface SubBubble {
		public void getImageUrl();
		public void onClick();
	}
}
