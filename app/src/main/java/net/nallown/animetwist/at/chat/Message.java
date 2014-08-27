package net.nallown.animetwist.at.chat;

/**
 * Created by Nasir on 27/08/2014.
 */
public final class Message {
	private String username;
	private String message;
	private int donation;
	private boolean admin;


	public Message(String message, String username, boolean admin, int donation){
		this.message = message;
		this.username = username;
		this.donation = donation;
		this.admin = admin;
	}

//	public static Message parseMessage(JSONObject msgJson) {
//
//	}

	public String getMessage() {
		return message;
	}
	public String getUser() {
		return username;
	}
	public boolean isAdmin() {
		return admin;
	}
	public int getDonation() {
		return donation;
	}
}
