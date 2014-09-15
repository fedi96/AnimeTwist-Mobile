package net.nallown.animetwist.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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

import net.nallown.animetwist.R;
import net.nallown.animetwist.adapters.MessageAdapter;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.UserFetcher;
import net.nallown.animetwist.at.chat.Message;
import net.nallown.animetwist.at.chat.SocketHandler;
import net.nallown.utils.Network;
import net.nallown.utils.NetworkReceiver;
import net.nallown.utils.Notifier;
import net.nallown.utils.websocket.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 26/08/2014.
 */

public class ChatFragment extends Fragment {
	private final String LOG_TAG = getClass().getSimpleName();

	ArrayList<Message> messages = null;
	MessageAdapter messageAdapter = null;

	ListView messageListView = null;
	EditText messageField = null;
	View view = null;

	User user = null;
	SocketHandler chatSocket = null;

	NetworkReceiver networkReceiver = new NetworkReceiver();

	private long pauseStart = 0;
	boolean pausing = false;
	boolean firstRun = true;
	int messageCount = 0;

	public ChatFragment() {
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static ChatFragment newInstance(/*int sectionNumber*/) {
		ChatFragment fragment = new ChatFragment();
//		Bundle args = new Bundle();
//		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Bundle data = getActivity().getIntent().getExtras();
		user = data.getParcelable("user");

		// Initialize components
		view = inflater.inflate(R.layout.fragment_chat, container, false);
		messageListView = (ListView) view.findViewById(R.id.messages_listview);

		messages = new ArrayList<Message>();
		messageAdapter = new MessageAdapter(getActivity(), R.layout.list_item_message, messages);

		messageField = (EditText) view.findViewById(R.id.messageInput);

		messageListView.setAdapter(messageAdapter);
		messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		messageListView.setStackFromBottom(true);

		socketManager();

		messageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
				String message = messageField.getText().toString().trim();

				if ((
					actionID == EditorInfo.IME_ACTION_SEND
					|| keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
				) && !message.isEmpty()){
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
	}

	private void socketManager() {
		chatSocket = new SocketHandler("wss://animetwist.net:9000");

		chatSocket.setSocketStates(new SocketHandler.SocketStates() {
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
			public void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String reason) {
				Notifier.showNotification("Socket Closed", reason, true, getActivity());
				Log.e(LOG_TAG, "socket closed");
			}

			@Override
			public void onTextMessage(final String payload) {
				if (messageCount >= 40 && firstRun) {
					firstRun = false;
				}

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
						if ((!firstRun && messageCount >= 40) || firstRun) {
							messages.add(Message.parseMessage(msgJson));
							messageAdapter.notifyDataSetChanged();
						}

						if (messageCount != 40)
							messageCount++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});

		chatSocket.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		pauseStart = System.currentTimeMillis();
		pausing = true;
	}

	@Override
	public void onResume() {
		if (System.currentTimeMillis() - pauseStart > (60 * 1000) * 30 && Network.isOnline(getActivity())) {
			chatSocket.reConnect();

			final SharedPreferences cachedUser = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);

			final String cachedUsername = cachedUser.getString("username", "");
			final String cachedPassword = cachedUser.getString("password", "");

			UserFetcher userFetcher = new UserFetcher(cachedUsername, cachedPassword);

			userFetcher.setRequestStates(new UserFetcher.RequestStates() {
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}

				@Override
				public void onStart() {
				}

				@Override
				public void onFinish(User user) {
					if (user != null) {
						JSONObject authJson = new JSONObject();
						try {
							authJson.put("type", "auth");
							authJson.put("token", user.getSessionID());
						} catch (JSONException e) {
							e.printStackTrace();
						}

						chatSocket.sendMessage(authJson.toString());
					}
				}
			});

			userFetcher.execute();
		}

		super.onResume();
		pausing = false;
	}
}