package net.nallown.animetwist.at;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private static User userInstance;

	private String username;
	private String password;
	private String sessionID;

	public static void setUserInstance(User user, Activity activity) {
		storeCachedUser(user, activity);
		userInstance = user;
	}

	public static User getInstance() {
		return userInstance;
	}

	public User(String username, String password, String sessionID) {
		this.username = username;
		this.password = password;
		this.sessionID = sessionID;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionID() {
		return sessionID;
	}

	public static void storeCachedUser(User user, Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = cachedUser.edit();

		editor.putString("username", user.getUsername());
		editor.putString("password", user.getPassword());
		editor.apply();
	}

	public static boolean cachedUserExists(Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);

		if (cachedUser.contains("username")) {
			return true;
		}
		return false;
	}

	public static void clearCachedUser(Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = cachedUser.edit();
		editor.clear();
		editor.apply();
	}

	public static User getCachedUser(Activity activity) {
		SharedPreferences cachedUser = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);

		return new User(cachedUser.getString("username", null), cachedUser.getString("password", null), null);
	}


	public User(Parcel pc) {
		username = pc.readString();
		password = pc.readString();
		sessionID = pc.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeString(username);
		pc.writeString(password);
		pc.writeString(sessionID);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel pc) {
			return new User(pc);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
}