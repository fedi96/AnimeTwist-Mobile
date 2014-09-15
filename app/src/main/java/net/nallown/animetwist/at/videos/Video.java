package net.nallown.animetwist.at.videos;

import org.json.JSONObject;

/**
 * Created by nasir on 15/09/14.
 */
public class Video {
	private String title;
	private String url;

	public Video(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public static Video parseVideo(JSONObject videosJson) {
		String title = videosJson.optString("name");
		String url = videosJson.optString("firstEpisode");

		return new Video(title, url);
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
}
