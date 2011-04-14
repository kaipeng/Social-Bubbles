import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.social.bubbles.fsquery.FoursquareFriendSearch;
import com.social.bubbles.fsquery.FoursquareSpecial;
import com.social.bubbles.fsquery.FoursquareSpecialsSearch;
import com.social.bubbles.fsquery.FoursquareUser;
import com.social.bubbles.fsquery.FoursquareVenue;
import com.social.bubbles.fsquery.FoursquareVenueSearch;


public class Main {
	public static void main(String args[]) throws IOException{
		if(!testVenueSearch()){
			System.out.println("testVenueSearch failed");
		}else {
			System.out.println("testVenueSearch passed");
		}
		if(!testFriendSearch()){
			System.out.println("testFriendSearch failed");
		}else {
			System.out.println("testFriendSearch passed");
		}
		if(!testSpecialsSearch()){
			System.out.println("testSpecialsSearch failed");
		}else {
			System.out.println("testSpecialsSearch passed");
		}
	}
	public static boolean testVenueSearch() throws IOException{
		byte[] buffer = new byte[(int) new File("venueTest").length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream("venueTest"));
			f.read(buffer);
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		String query = new String(buffer);
		
		FoursquareVenueSearch vs = new Gson().fromJson(query,FoursquareVenueSearch.class);
		LinkedList<FoursquareVenue> venues = vs.getVenuesAroundUser();
		//add more tests here later
		for (FoursquareVenue venue : venues){
			if (venue.getName()==null ||
			venue.getAddress()==null ||
			venue.getCity()==null ||
			venue.getState()==null) {
				return false;
			}
		}
		return true;
	}
	public static boolean testFriendSearch() throws IOException{
		byte[] buffer = new byte[(int) new File("friendTest").length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream("friendTest"));
			f.read(buffer);
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		String query = new String(buffer);
		FoursquareFriendSearch fs = new Gson().fromJson(query,FoursquareFriendSearch.class);
		LinkedList<FoursquareUser> friends = fs.getFriends();
		//add more tests here later
		for (FoursquareUser friend : friends){
			if (friend.getFirstName()==null ||
				friend.getGender()==null ||
				friend.getHomeCity()==null ||
				friend.getPhoto()==null){
				return false;
			}
		}
		return true;
	}
	public static boolean testSpecialsSearch() throws IOException{
		byte[] buffer = new byte[(int) new File("specialsTest").length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream("specialsTest"));
			f.read(buffer);
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		String query = new String(buffer);
		FoursquareSpecialsSearch fs = new Gson().fromJson(query,FoursquareSpecialsSearch.class);
		LinkedList<FoursquareSpecial> specials = fs.getSpecials();
		for (FoursquareSpecial special : specials){
			if (special.getId()==null ||
				special.getType()==null ||
				special.getMessage()==null ||
				special.getVenue()==null ||
				special.getVenue().getAddress()==null){
				return false;
			}
		}
		
		return true;
	}
}
