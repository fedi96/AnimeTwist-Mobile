package net.nallown.animetwist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.nallown.animetwist.at.Listener.SocketListener;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.chat.ChatSocketHandler;
import net.nallown.animetwist.at.chat.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocket;

/**
 * Created by Nasir on 26/08/2014.
 */

public class ChatFragment extends Fragment{
	private final String LOG_TAG = getClass().getSimpleName();
	User user = null;

	ArrayList<Message> messages = null;
	MessageAdapter messageAdapter = null;
	ListView messageListview = null;
	EditText messageField = null;

	ChatSocketHandler chatSocket = null;

	View view;

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
					JSONObject authJson = new JSONObject();
					JSONObject msgJson = new JSONObject();
					try {
						authJson.put("type", "msg");
						authJson.put("msg", message);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					chatSocket.sendMessage(authJson.toString());
					messageField.setText(null);
					return true;
				}

				return false;
			}
		});

		socketManager();

		// Dummy messages
//		for (int i = 0; i < 13; i++) {
//			Random random = new Random();
//			String randMsg = UUID.randomUUID().toString();
//
//			messages.add(new Message("Test USER" + i, randMsg, true, random.nextInt(100)));
//		}

		messageAdapter.notifyDataSetChanged();
		return view;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ChatActivity) activity).onSectionAttached(
				getArguments().getInt(ARG_SECTION_NUMBER));
	}

	private void socketManager(){
		chatSocket = new ChatSocketHandler(new SocketListener() {
			@Override
			public void onOpen() {
				JSONObject authJson = new JSONObject();
				JSONObject msgJson = new JSONObject();
				try {
					authJson.put("type", "auth");
					authJson.put("token", user.getSessionID());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				chatSocket.sendMessage(authJson.toString());
			}

			@Override
			public void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String reason) {
				Log.i(LOG_TAG, "Code: " + code + "; Reason: " + reason);
			}

			@Override
			public void onTextMessage(final String payload) {
				if (!payload.equals("keep-alive")) {
					try {
						final JSONObject msgJson = new JSONObject(payload);

						if (!msgJson.has("auth")) {
							messages.add(Message.parseMessage(msgJson));
							messageAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onRawTextMessage(byte[] payload) {}
			@Override
			public void onBinaryMessage(byte[] payload) {}
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}

}