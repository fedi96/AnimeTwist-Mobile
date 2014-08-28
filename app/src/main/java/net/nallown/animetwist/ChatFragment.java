package net.nallown.animetwist;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import net.nallown.utils.NetworkReceiver;
import net.nallown.utils.States.NetworkStates;
import net.nallown.utils.States.SocketStates;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.chat.ChatSocketHandler;
import net.nallown.animetwist.at.chat.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import net.nallown.utils.Network;
import net.nallown.utils.websocket.WebSocket;

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

        messageField = (EditText) view.findViewById(R.id.messageInput);

        networkReceiver = new NetworkReceiver(new NetworkStates() {
            @Override
            public void onConnectionChange(int ConnectionType, String connectionMessage) {
                if (ConnectionType == Network.TYPE_NON) {
                    chatSocket.getSocket().disconnect();

                    Toast.makeText(getActivity(), "Lost connection", Toast.LENGTH_LONG).show();
                } else {
                    chatSocket.reConnect();

                    Toast.makeText(getActivity(), connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

		messageListview.setAdapter(messageAdapter);
		messageListview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		messageListview.setStackFromBottom(true);

        socketManager();

		messageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                String message = messageField.getText().toString().trim();

                if (actionID == EditorInfo.IME_ACTION_SEND && !message.isEmpty()) {
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

		return view;
	}

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        int connectionStatus = Network.getConnectivityStatus(getActivity());
        String connectionMessage = Network.getStatusMessage(getActivity());

        @Override
        public void onReceive(Context context, Intent intent) {
            if (connectionStatus == Network.TYPE_NON) {
                chatSocket.getSocket().disconnect();

                Toast.makeText(getActivity(), "Lost connection", Toast.LENGTH_LONG).show();
            } else {
                chatSocket.reConnect();

                Toast.makeText(getActivity(), connectionMessage, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ChatActivity) activity).onSectionAttached(
				getArguments().getInt(ARG_SECTION_NUMBER));
	}

	private void socketManager(){
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
			}

			@Override
			public void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String reason) {
				Log.i(LOG_TAG, "Code: " + code + "; Reason: " + reason);
			}

			@Override
			public void onTextMessage(final String payload) {
                if (payload.equals("keep-alive")) {
                    return;
                }

                try {
                    JSONObject msgJson = new JSONObject(payload);

                    if (!msgJson.has("auth")) {
                        messages.add(Message.parseMessage(msgJson));
                        messageAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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