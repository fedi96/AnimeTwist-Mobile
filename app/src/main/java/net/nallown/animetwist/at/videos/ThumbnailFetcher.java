package net.nallown.animetwist.at.videos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Nasir on 17/09/2014.
 */
public class ThumbnailFetcher extends AsyncTask{
	private String thumbnailUrl;

	private RequestStates requestStates;

	private Bitmap thumbnail;

	public ThumbnailFetcher(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public void setRequestStates(RequestStates requestStates) {
		this.requestStates = requestStates;
		this.execute();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(Object[] objects) {
		try {
			URL url = new URL(thumbnailUrl);
			thumbnail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		} catch (IOException e) {
			requestStates.onError(e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		requestStates.onFinish(thumbnail);
	}

	public static interface RequestStates{

		public void onError(Exception e);
		public void onFinish(Bitmap thumbnail);

	}
}
