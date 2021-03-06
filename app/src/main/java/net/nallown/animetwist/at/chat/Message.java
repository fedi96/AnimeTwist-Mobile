package net.nallown.animetwist.at.chat;

import android.text.Html;

import org.json.JSONObject;

/**
 * Created by Nasir on 27/08/2014.
 */
public final class Message {
	private static boolean notificationEnabled = false;
	private String username;
	private String message;
	private boolean donator;
	private boolean admin;

	public Message(String username, String message, boolean admin, boolean donation) {
		this.message = message;
		this.username = username;
		this.donator = donation;
		this.admin = admin;
	}

	public Message(String message) {
		this.message = message;
		this.username = "Notice";
		this.donator = false;
		this.admin = false;
	}

	public static Message parseMessage(JSONObject msgJson) {
		String username = msgJson.optString("username");
		String message = Html.fromHtml(msgJson.optString("msg")).toString();
		boolean donation = (msgJson.optInt("donator") == 1);
		boolean op = (msgJson.optInt("op") == 1);

		return new Message(username, message, op, donation);
	}

	public static Message notify(String message) {
		return new Message(message);
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

	public boolean isDonator() {
		return donator;
	}

	public static boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	public static void setNotificationEnabled(boolean status) {
		notificationEnabled = status;
	}
}
