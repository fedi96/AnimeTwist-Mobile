package net.nallown.animetwist.at;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class User implements Parcelable {
	private final String LOG_TAG = getClass().getSimpleName();

	private String Username;
	private String Password;
	private String SessionID;
	private boolean LoginState;

	public String getUsername() {
		return Username;
	}
	public String getSessionID() {
		return SessionID;
	}
	public String getPassword() {
		return Password;
	}
	public boolean getLoginState() {
		return LoginState;
	}

	public User(String username, String password){
		this.Username = username;
		this.Password = password;
	}

	public boolean login() throws ExecutionException, InterruptedException {
		FetchUser UserFetcher = new FetchUser();
		return UserFetcher.execute().get();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flags) {
		pc.writeString(Username);
		pc.writeString(Password);
		pc.writeString(SessionID);
		pc.writeInt( LoginState ? 1:0 );
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
		Password = pc.readString();
		SessionID = pc.readString();
		LoginState = ( pc.readInt() == 1);
	}

	private class FetchUser extends AsyncTask<Void, Void, Boolean> {
		String responseStr;

		@Override
		protected Boolean doInBackground(Void... voids) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("https://animetwist.net/login");

			HttpResponse response = null;

			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("username", Username));
			postData.add(new BasicNameValuePair("password", Password));
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(postData));

				response = httpClient.execute(httpPost);
				responseStr = EntityUtils.toString(response.getEntity());

				JSONObject responseJson = new JSONObject(responseStr);
				String Status = responseJson.getString("res");

				if (Status.equals("success")) {
					SessionID = responseJson.getString("token");
					LoginState = true;
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JSONException e) {
				Log.e(LOG_TAG, e.getMessage());
			}

			return LoginState;
		}
	}
}