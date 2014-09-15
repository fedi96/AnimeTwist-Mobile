package net.nallown.animetwist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.nallown.animetwist.R;
import net.nallown.animetwist.at.videos.Video;

import java.util.List;

/**
 * Created by nasir on 15/09/14.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

	private int resource;

	public VideoAdapter(Context context, int resource, List<Video> items) {
		super(context, resource, items);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout videoView;
		Video video = getItem(position);

		if (convertView == null) {
			videoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater;

			layoutInflater = (LayoutInflater) getContext().getSystemService(inflater);
			layoutInflater.inflate(resource, videoView, true);
		} else {
			videoView = (LinearLayout) convertView;
		}

		TextView videoTitle = (TextView) videoView.findViewById(R.id.video_title);
		ImageView videoThumbnail = (ImageView) videoView.findViewById(R.id.video_thumbnail);

		videoTitle.setText(video.getTitle());

		return videoView;
	}
}