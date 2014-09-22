package net.nallown.animetwist.at.series;

import java.util.ArrayList;

/**
 * Created by Nasir on 19/09/2014.
 */
public class Series {
	private String title;
	private ArrayList<Episode> episodes;

	public Series(String title, ArrayList<Episode> episodes) {
		this.episodes = new ArrayList<Episode>(episodes);
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public Episode getEpisode(int episodeNumber) {
		return episodes.get(episodeNumber - 1);
	}

	public static class Episode {
		public String URL;

		public Episode(String url) {
			this.URL = url;
		}
	}
}
