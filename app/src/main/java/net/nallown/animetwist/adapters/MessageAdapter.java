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
import net.nallown.animetwist.at.chat.Message;

import java.util.List;

/**
 * Created by Nasir on 27/08/2014.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

	private int resource;
	private int[] colors;

	public MessageAdapter(Context context, int resource, List<Message> items) {
		super(context, resource, items);
		this.resource = resource;
		colors = new int[]{
				context.getResources().getColor(R.color.black),
				context.getResources().getColor(R.color.gray_dark_extreme)
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int colorPos = position % colors.length;
		LinearLayout messageView;
		Message msg = getItem(position);

		if (convertView == null) {
			messageView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater;

			layoutInflater = (LayoutInflater) getContext().getSystemService(inflater);
			layoutInflater.inflate(resource, messageView, true);
		} else {
			messageView = (LinearLayout) convertView;
		}

		TextView usernameField = (TextView) messageView.findViewById(R.id.usernameHolder);
		TextView messageField = (TextView) messageView.findViewById(R.id.messageHolder);
		ImageView donationBadge = (ImageView) messageView.findViewById(R.id.donationBadge);

		usernameField.setText(msg.getUser());
		messageField.setText(msg.getMessage());

		// RESET
		usernameField.setTextColor(getContext().getResources().getColor(R.color.gray_light));
		messageField.setTextColor(getContext().getResources().getColor(R.color.gray_light));
		donationBadge.setVisibility(View.GONE);

		if (msg.isAdmin()) {
			usernameField.setTextColor(getContext().getResources().getColor(R.color.red));
		} else if (msg.getUser().equals("Notice")) {
			usernameField.setTextColor(getContext().getResources().getColor(R.color.gray));
			messageField.setTextColor(getContext().getResources().getColor(R.color.gray));
		}

		if (msg.isDonator()) {
			donationBadge.setVisibility(View.VISIBLE);
		}

		messageView.setBackgroundColor(colors[colorPos]);
		return messageView;
	}

}
