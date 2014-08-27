package net.nallown.animetwist.at;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private final String LOG_TAG = getClass().getSimpleName();

	private String Username;
	private String SessionID;

	public String getUsername() {
		return Username;
	}
	public String getSessionID() {
		return SessionID;
	}

	public User(String username, String sessionID){
		this.Username = username;
		this.SessionID = sessionID;
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

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel pc) {
			return new User(pc);
		}
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public User(Parcel pc){
		Username = pc.readString();
		SessionID = pc.readString();
	}

}