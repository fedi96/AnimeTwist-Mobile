package net.nallown.animetwist.at.videos;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nasir on 17/09/2014.
 */
public class VideoFetcher extends AsyncTask{
	private final String REQUEST_URL = "http://5.175.138.115/at/anime.php";

	private String payload = null;
	private RequestStates requestStates;

	public VideoFetcher(RequestStates requestStates) {
		this.requestStates = requestStates;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		requestStates.onStart();
	}

	@Override
	protected Object doInBackground(Object[] objects) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(REQUEST_URL);


		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			payload = EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException e) {
			requestStates.onError(e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		ArrayList<Video> videoArrayList = new ArrayList<Video>();

		try {
			JSONArray videosJsonArray = new JSONArray(payload);

			for (int i = 0; i < videosJsonArray.length(); i++) {
				JSONObject videoJson = videosJsonArray.getJSONObject(i);
				try {
					videoArrayList.add(Video.parseVideo(videoJson));
				} catch (IOException e) {
					requestStates.onError(e);
				}
			}
		} catch (JSONException e) {
			requestStates.onError(e);
		}

		requestStates.onFinish(videoArrayList);
	}

	public static interface RequestStates{

		public void onError(Exception e);
		public void onStart();
		public void onFinish(ArrayList<Video> videoArrayList);

	}
}
