package net.nallown.animetwist.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.nallown.animetwist.R;
import net.nallown.animetwist.activities.SeriesActivity;
import net.nallown.animetwist.adapters.VideoAdapter;
import net.nallown.animetwist.at.videos.ThumbnailFetcher;
import net.nallown.animetwist.at.videos.Video;
import net.nallown.animetwist.at.videos.VideoFetcher;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SeriesListFragment extends Fragment implements VideoFetcher.RequestStates {
	private final String LOG_TAG = getClass().getSimpleName();

	private ArrayList<Video> videos = null;
	private ArrayList<Video> allVideos = null;
	private VideoAdapter videoAdapter = null;

	private EditText searchEditText;
	private LinearLayout fetchingVideos;

    public SeriesListFragment() {
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_videos, container, false);

	    ListView videoListView = (ListView) view.findViewById(R.id.videos_listitem);
		searchEditText = (EditText) view.findViewById(R.id.videos_search);
	    fetchingVideos = (LinearLayout) view.findViewById(R.id.fetching_videos);

	    videos = new ArrayList<Video>();
	    allVideos = new ArrayList<Video>();
	    videoAdapter = new VideoAdapter(getActivity(), R.layout.item_video, videos);

	    videoListView.setAdapter(videoAdapter);
		videoListView.setOnItemClickListener(onVideoClick);

		VideoFetcher videoFetcher = new VideoFetcher(this);
		videoFetcher.execute();

	    searchEditText.addTextChangedListener(onSearchEdit);

        return view;
    }

	AdapterView.OnItemClickListener onVideoClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			String seriesFolder = videos.get(i).getFolder();
			Intent videoIntent = new Intent(getActivity(), SeriesActivity.class);
			videoIntent.putExtra("folder_name", seriesFolder);

			startActivity(videoIntent);
		}
	};

	TextWatcher onSearchEdit = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
		}

		@Override
		public void onTextChanged(CharSequence searchInput, int i, int i2, int i3) {
			String searchString = searchInput.toString().toLowerCase().trim();
			sortListView(searchString);
		}

		@Override
		public void afterTextChanged(Editable editable) {
		}
	};

	private void sortListView(String searchStr) {
		videos.clear();
		for (Video video: allVideos) {
			if (video.getTitle().toLowerCase().contains(searchStr)) {
				videos.add(video);
			}
		}

		videoAdapter.notifyDataSetChanged();
	}

	@Override
	public void onFetchError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onFetchStart() {
		fetchingVideos.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFetchFinish(ArrayList<Video> videoArrayList) {
		fetchingVideos.setVisibility(View.GONE);
		searchEditText.setVisibility(View.VISIBLE);

		for (final Video video : videoArrayList) {
			ThumbnailFetcher thumbnailFetcher = new ThumbnailFetcher(video.getThumbnailUrl());
			thumbnailFetcher.setRequestStates(new ThumbnailFetcher.RequestStates() {
				@Override
				public void onError(Exception e) {
				}

				@Override
				public void onFinish(Bitmap thumbnailBitmap) {
					video.setThumbnail(thumbnailBitmap);
					videos.add(video);
					allVideos.add(video);

					videoAdapter.notifyDataSetChanged();
				}
			});
			thumbnailFetcher.execute();
		}
	}
}
