package net.nallown.animetwist.at;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasir on 26/08/2014.
 */
public class UserFetcher extends AsyncTask<Void, Void, Void> {
	private RequestStates listener;
	private String responseStr;
	private String username;
	private String password;

	public UserFetcher(String username, String password) {
		this.username = username;
		this.password = password;
		this.listener = listener;
		execute();
	}

	public void setRequestStates(RequestStates listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		listener.onStart();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... voids) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("https://animetwist.net/login");

		HttpResponse response = null;

		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		postData.add(new BasicNameValuePair("username", username));
		postData.add(new BasicNameValuePair("password", password));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postData));

			response = httpClient.execute(httpPost);
			responseStr = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			responseStr = "{'res' : 'exception', 'message' : 'Failed to connect to Anime Twist server.'}";
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		listener.onFinish(responseStr);
		super.onPostExecute(aVoid);
	}


	public static interface RequestStates {
		public void onError(Exception e);

		public void onFinish(String result);

		public void onStart();
	}
}