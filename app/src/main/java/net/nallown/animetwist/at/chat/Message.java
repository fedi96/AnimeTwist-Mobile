package net.nallown.animetwist.at.chat;

import android.text.Html;

import org.json.JSONObject;

/**
 * Created by Nasir on 27/08/2014.
 */
public final class Message {
	private String username;
	private String message;
	private boolean donation;
	private boolean admin;

	public Message(String username, String message, boolean admin, boolean donation){
		this.message = message;
		this.username = username;
		this.donation = donation;
		this.admin = admin;
	}

	public static Message parseMessage(JSONObject msgJson) {
		String username = msgJson.optString("username");
		String message = Html.fromHtml(msgJson.optString("msg")).toString();
		boolean donation = (msgJson.optInt("donation") == 1);
		boolean op = (msgJson.optInt("op") == 1);

		return new Message(username, message, op, donation);
	}

	public String getMessage() {
		return message;
	}
	public String getUser() {
		return username;
	}
	public boolean isAdmin() {
		return admin;
	}
	public boolean getDonation() {
		return donation;
	}
}
