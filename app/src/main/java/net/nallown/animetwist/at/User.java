package net.nallown.animetwist.at;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel pc) {
			return new User(pc);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
	private final String LOG_TAG = getClass().getSimpleName();
	private String Username;
	private String SessionID;

	public User(String username, String sessionID) {
		this.Username = username;
		this.SessionID = sessionID;
	}

	public User(Parcel pc) {
		Username = pc.readString();
		SessionID = pc.readString();
	}

	public String getUsername() {
		return Username;
	}

	public String getSessionID() {
		return SessionID;
	}

	public String getJsonAuthToken() {
		JSONObject authJson = new JSONObject();

		try {
			authJson.put("type", "auth");
			authJson.put("token", SessionID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return authJson.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeString(Username);
		pc.writeString(SessionID);
	}

	public static void storeToCache(String username, String password, Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = cachedUser.edit();

		editor.putString("username", username);
		editor.putString("password", password);
		editor.apply();
	}

	public static boolean cachedUserExists(Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);

		if (cachedUser.contains("username")) {
			return true;
		}
		return false;
	}
}