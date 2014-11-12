package net.nallown.animetwist.at;

import android.os.AsyncTask;
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

/**
 * Created by Nasir on 26/08/2014.
 */
public class UserFetcher extends AsyncTask<Void, Void, Void> {
	private User user;
	private RequestStates listener;
	private String payload;
	private String username;
	private String password;

	public UserFetcher(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void setRequestStates(RequestStates listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		listener.onFetchStart();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... voids) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://twist.moe/login");

		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		postData.add(new BasicNameValuePair("username", username));
		postData.add(new BasicNameValuePair("password", password));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postData));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			payload = EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException e) {
			listener.onFetchError(e);
		}

		Log.d("PAYLOAD", payload);
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		try {
			JSONObject responseJson = new JSONObject(payload);
			String Status = responseJson.getString("res");

			if (Status.equals("success")) {
				String sessionID = responseJson.getString("token");
				user = new User(username, password, sessionID);
			}
		} catch (JSONException e) {
			listener.onFetchError(e);
		}

		listener.onFetchFinish(user);
		super.onPostExecute(aVoid);
	}


	public static interface RequestStates {
		public void onFetchError(Exception e);

		public void onFetchFinish(User user);

		public void onFetchStart();
	}
}