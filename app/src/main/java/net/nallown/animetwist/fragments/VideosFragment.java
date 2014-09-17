package net.nallown.animetwist.fragments;



import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import net.nallown.animetwist.R;
import net.nallown.animetwist.adapters.VideoAdapter;
import net.nallown.animetwist.at.videos.Video;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class VideosFragment extends Fragment {

	private View view;

	private ArrayList<Video> videos = null;
	private ArrayList<Video> allVideos = null;
	private VideoAdapter videoAdapter = null;

	private ListView videoListView;
	private EditText searchEditText;

    public VideosFragment() {
        // Required empty public constructor
    }

	public static VideosFragment newInstance(/*int sectionNumber*/) {
		VideosFragment fragment = new VideosFragment();
//		Bundle args = new Bundle();
//		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//		fragment.setArguments(args);
		return fragment;
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.fragment_videos, container, false);
	    videoListView = (ListView) view.findViewById(R.id.videos_listitem);
		searchEditText = (EditText) view.findViewById(R.id.videos_search);

	    videos = new ArrayList<Video>();
	    allVideos = new ArrayList<Video>();
	    videoAdapter = new VideoAdapter(getActivity(), R.layout.list_item_video, videos);

	    videoListView.setAdapter(videoAdapter);
	    videoListView.setTextFilterEnabled(true);

	    for (int i = 0; i < 20; i++) {
		    allVideos.add(new Video("Video " + i, ""));
		    videos.add(new Video("Video " + i, ""));
		    videoAdapter.notifyDataSetChanged();
	    }

	    searchEditText.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
		    }

		    @Override
		    public void onTextChanged(CharSequence string, int i, int i2, int i3) {
			    String searchString = string.toString().toLowerCase().trim();

			    videos.clear();
			    for (Video video: allVideos) {
				    if (video.getTitle().toLowerCase().contains(searchString)) {
					    videos.add(video);
				    }
			    }

			    videoAdapter.notifyDataSetChanged();
		    }

		    @Override
		    public void afterTextChanged(Editable editable) {
		    }
	    });

        return view;
    }


}
