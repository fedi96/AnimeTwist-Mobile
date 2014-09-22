package net.nallown.animetwist.at.series;

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
 * Created by Nasir on 19/09/2014.
 */
public class SeriesFetcher extends AsyncTask {
	private final String REQUEST_URL = "http://5.175.138.115/at/anime.php?folder=";

	private String payload = null;
	private String folderName;
	private RequestStates requestStates;

	public SeriesFetcher(String folderName) {
		this.folderName = folderName;
	}

	public void setRequestStates(RequestStates requestStates) {
		this.requestStates = requestStates;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		requestStates.onFetchStart();
	}

	@Override
	protected Object doInBackground(Object[] objects) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(REQUEST_URL + folderName);

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			payload = EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException e) {
			requestStates.onFetchError(e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		String title = null;
		ArrayList<Series.Episode> episodeList = new ArrayList<Series.Episode>();

		try {
			JSONObject seriesJsonObj = new JSONObject(payload);
			JSONArray episodesArray = seriesJsonObj.optJSONArray("episodes");
			title = seriesJsonObj.optString("title");

			for (int i = 0; i < episodesArray.length(); i++) {
				episodeList.add(new Series.Episode(episodesArray.getString(i)));
			}

		} catch (JSONException e) {
			requestStates.onFetchError(e);
		}

		requestStates.onFetchFinish(new Series(title, episodeList));
	}

	public static interface RequestStates{

		public void onFetchError(Exception e);
		public void onFetchStart();
		public void onFetchFinish(Series series);

	}
}
