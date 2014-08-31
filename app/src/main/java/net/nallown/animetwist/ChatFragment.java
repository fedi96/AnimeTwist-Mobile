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

import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.chat.ChatSocketHandler;
import net.nallown.animetwist.at.chat.Message;
import net.nallown.utils.NetworkReceiver;
import net.nallown.utils.Notifier;
import net.nallown.utils.States.SocketStates;
import net.nallown.utils.websocket.WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 26/08/2014.
 */

public class ChatFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private final String LOG_TAG = getClass().getSimpleName();
	User user = null;
	ArrayList<Message> messages = null;
	MessageAdapter messageAdapter = null;
	ListView messageListview = null;
	EditText messageField = null;
	ChatSocketHandler chatSocket = null;
	NetworkReceiver networkReceiver = new NetworkReceiver();
	boolean pausing = false;
	boolean clearedBuffer = false;
	int messageCount = 0;
	View view;

	public ChatFragment() {
	}

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

		messageField = (EditText) view.findViewById(R.id.messageInput);

		messageListview.setAdapter(messageAdapter);
		messageListview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		messageListview.setStackFromBottom(true);

		socketManager();

		messageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
				String message = messageField.getText().toString().trim();

				if (
						(actionID == EditorInfo.IME_ACTION_SEND
								|| keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
								&& !message.isEmpty()) {
					JSONObject msgJson = new JSONObject();
					try {
						msgJson.put("type", "msg");
						msgJson.put("msg", message);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					chatSocket.sendMessage(msgJson.toString());
					messageField.setText(null);
					return true;
				}

				return false;
			}
		});

		networkReceiver.setOnNetworkChangeListener(new NetworkReceiver.onNetworkChangeListener() {
			@Override
			public void onNetworkChange(boolean connected) {
				if (connected) {
					chatSocket.reConnect();
					messageField.setEnabled(true);
					messageField.setHint("...");
				} else {
					messageField.setEnabled(false);
					messageField.setHint("No network connection...");

					messages.add(Message.notify("Lost network connection"));

					messageAdapter.notifyDataSetChanged();
					Notifier.showNotification(
							"Connection lost...", "Anime Twist has lost connection.",
							false, getActivity()
					);
				}
			}
		});

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ChatActivity) activity).onSectionAttached(
				getArguments().getInt(ARG_SECTION_NUMBER));
	}

	private void socketManager() {
		chatSocket = new ChatSocketHandler(new SocketStates() {
			@Override
			public void onOpen() {
				JSONObject authJson = new JSONObject();
				try {
					authJson.put("type", "auth");
					authJson.put("token", user.getSessionID());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				chatSocket.sendMessage(authJson.toString());

				messageCount = 0;
			}

			@Override
			public void onClose(WebSocketCloseNotification code, String reason) {
				Notifier.showNotification("Socket Closed", reason, true, getActivity());
				Log.e(LOG_TAG, "socket closed");
			}

			@Override
			public void onTextMessage(final String payload) {
				if (payload.equals("keep-alive")) {
					return;
				}

				try {
					JSONObject msgJson = new JSONObject(payload);
					String msg = msgJson.optString("msg");
					String msgUser = msgJson.optString("username");

					// Needs option amd keywords
					if (msg.toLowerCase().contains(user.getUsername().toLowerCase())
							&& !msgUser.toLowerCase().equals(user.getUsername().toLowerCase())
							&& pausing) {
						Notifier.showNotification(
								msgUser + " mentioned you",
								msg, true, getActivity()
						);
					}

					if (!msgJson.has("auth")) {
						if (messages.size() >= 40 && messageCount < 40) {
							messageCount++;
							return;
						}

						messages.add(Message.parseMessage(msgJson));
						messageAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRawTextMessage(byte[] payload) {
			}

			@Override
			public void onBinaryMessage(byte[] payload) {
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		pausing = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		pausing = false;
	}
}