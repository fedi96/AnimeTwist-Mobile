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
		final Video video = getItem(position);

		if (convertView == null) {
			videoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater;

			layoutInflater = (LayoutInflater) getContext().getSystemService(inflater);
			layoutInflater.inflate(resource, videoView, true);
		} else {
			videoView = (LinearLayout) convertView;
		}

		final TextView videoTitle = (TextView) videoView.findViewById(R.id.video_title);
		final TextView ongoing_tag = (TextView) videoView.findViewById(R.id.ongoing_tag);
		final ImageView thumbnail = (ImageView) videoView.findViewById(R.id.video_thumbnail);

		if (video.isOngoing()) {
			ongoing_tag.setVisibility(View.VISIBLE);
		} else {
			ongoing_tag.setVisibility(View.GONE);
		}

		thumbnail.setImageBitmap(video.getThumbnail());
		thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
		thumbnail.setAlpha(0.9f);

		videoTitle.setText(video.getTitle());

		return videoView;
	}
}