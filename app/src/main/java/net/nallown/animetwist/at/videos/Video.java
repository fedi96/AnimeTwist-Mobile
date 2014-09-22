package net.nallown.animetwist.at.videos;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nasir on 15/09/14.
 */
public class Video {
	private String title;
	private String folder;
	private String thumbnailUrl;
	private boolean ongoing;
	private Bitmap thumbnail;

	public Video(String title, boolean ongoing, String folder, String thumbnailUrl) {
		this.title = title;
		this.ongoing = ongoing;
		this.folder = folder;
		this.thumbnailUrl = thumbnailUrl;
	}

	public static Video parseVideo(JSONObject videoJson) throws IOException {
		String title = videoJson.optString("title");
		boolean ongoing = videoJson.optBoolean("ongoing");
		String folder = videoJson.optString("folder");
		String thumbnailUrl = videoJson.optString("thumbnail");

		return new Video(title, ongoing, folder, thumbnailUrl);
	}

	public String getTitle() {
		return title;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getFolder() {
		return folder;
	}

	public boolean isOngoing() {
		return ongoing;
	}

	public Bitmap getThumbnail() { return thumbnail; }

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
}
