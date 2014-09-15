package net.nallown.animetwist.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.nallown.animetwist.R;
import net.nallown.animetwist.adapters.MessageAdapter;
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
	private VideoAdapter videoAdapter= null;

	private ListView videoListView;

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

	    videos = new ArrayList<Video>();
	    videoAdapter = new VideoAdapter(getActivity(), R.layout.list_item_video, videos);

	    videoListView.setAdapter(videoAdapter);

	    for (int i = 0; i < 20; i++) {
		    videos.add(new Video("Video " + i, ""));
		    videoAdapter.notifyDataSetChanged();
	    }

        return view;
    }


}
