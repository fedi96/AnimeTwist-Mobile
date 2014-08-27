package net.nallown.animetwist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.chat.Message;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Nasir on 26/08/2014.
 */

public class ChatFragment extends Fragment {
	View view;
	User user = null;
	ListView messageListview = null;

	MessageAdapter messageAdapter = null;
	ArrayList<Message> messages = null;

	EditText messageField = null;

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static ChatFragment newInstance(int sectionNumber) {
		ChatFragment fragment = new ChatFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ChatFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Bundle data = getActivity().getIntent().getExtras();
		user = (User) data.getParcelable("user");

		// Initialize components
		view = inflater.inflate(R.layout.fragment_chat, container, false);
		messageListview = (ListView) view.findViewById(R.id.messages_listview);

		messages = new ArrayList<Message>();
		messageAdapter = new MessageAdapter(getActivity(), R.layout.list_item_message, messages);

		messageListview.setAdapter(messageAdapter);
		messageListview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		messageListview.setStackFromBottom(true);

		messageField = (EditText) view.findViewById(R.id.messageInput);
		messageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
				String message = messageField.getText().toString().trim();

				if (actionID == EditorInfo.IME_ACTION_SEND && !message.isEmpty()){
					messages.add(new Message(user.getUsername(), message, false, 0));
					messageField.setText("");

					messageAdapter.notifyDataSetChanged();
					return true;
				}

				return false;
			}
		});

		// Dummy messages
		for (int i = 0; i < 13; i++) {
			Random random = new Random();
			String randMsg = UUID.randomUUID().toString();

			messages.add(new Message("Test USER" + i, randMsg, true, random.nextInt(100)));
		}

		messageAdapter.notifyDataSetChanged();
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ChatActivity) activity).onSectionAttached(
				getArguments().getInt(ARG_SECTION_NUMBER));
	}
}