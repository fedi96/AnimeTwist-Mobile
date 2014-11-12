package net.nallown.animetwist.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import net.nallown.animetwist.R;
import net.nallown.animetwist.adapters.MessageAdapter;
import net.nallown.animetwist.at.User;
import net.nallown.animetwist.at.chat.ChatSocket;
import net.nallown.animetwist.at.chat.Message;
import net.nallown.utils.NetworkReceiver;
import net.nallown.utils.Notifier;
import net.nallown.utils.websocket.WebSocket;

import java.util.ArrayList;

/**
 * Created by Nasir on 26/08/2014.
 */
public class ChatFragment extends Fragment
	implements NetworkReceiver.onNetworkChangeListener,
	ChatSocket.SocketStates {
	private final String LOG_TAG = getClass().getSimpleName();
	private final String DRAWER_STATE_TITLE = "DRAWER_STATE";

	private SharedPreferences sharedPreferences;
	private boolean mUserLearnedDrawer;

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private View mFragmentContainerView;

	private MessageAdapter messageAdapter = null;

	private ListView messageListView = null;
	private EditText messageField = null;
	private View view = null;

	private ChatSocket chatSocket = null;

	public ChatFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sharedPreferences.getBoolean(DRAWER_STATE_TITLE, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_chat, container, false);
		messageListView = (ListView) view.findViewById(R.id.messages_listview);

		messageAdapter = new MessageAdapter(getActivity(), R.layout.item_message, new ArrayList<Message>());

		messageField = (EditText) view.findViewById(R.id.messageInput);

		messageListView.setAdapter(messageAdapter);
		messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		messageListView.setStackFromBottom(true);

		chatSocket = new ChatSocket("ws://twist.moe:9003/", getActivity());
		chatSocket.setSocketStates(this);
		chatSocket.connect(getActivity());

		messageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
				String message = messageField.getText().toString().trim();

				if ((
					actionID == EditorInfo.IME_ACTION_SEND
					|| keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
				) && !message.isEmpty()){
					chatSocket.sendTextMessage(message);
					messageField.setText(null);
					return true;
				}

				return false;
			}
		});

		NetworkReceiver networkReceiver = new NetworkReceiver();
		networkReceiver.setOnNetworkChangeListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		super.onPause();
		Message.setNotificationEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		Message.setNotificationEnabled(false);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);

		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}


				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		if (!mUserLearnedDrawer) {
			mDrawerLayout.openDrawer(mFragmentContainerView);

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(DRAWER_STATE_TITLE, true);
			editor.apply();
		}

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public View getmFragmentContainerView() {
		return mFragmentContainerView;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	@Override
	public void onNetworkChange(boolean connected) {
		if (connected) {
			chatSocket.reConnect(getActivity());
			messageField.setEnabled(true);
			messageField.setHint(getResources().getString(R.string.chat_enabled_hint));
		} else {
			messageField.setEnabled(false);
			messageField.setHint("No network connection...");

			messageAdapter.addMessage(Message.notify("Lost network connection"));

			messageAdapter.notifyDataSetChanged();
			Notifier.showNotification(
					"Connection lost...", "Anime Twist has lost connection.",
					false, getActivity(), null
			);
		}
	}

	@Override
	public void onSocketOpen() {
		if (messageField.getHint().toString().equals(getResources().getString(R.string.chat_failed_hint))) {
			messageField.setHint(getResources().getString(R.string.chat_enabled_hint));
			messageField.setEnabled(true);
		}
		messageAdapter.clear();

		chatSocket.sendUser(User.getInstance());
		messageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSocketClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String reason) {
		Notifier.showNotification("Socket Closed", reason, true, getActivity(), null);
		Log.e(LOG_TAG, "socket closed");
	}

	@Override
	public void onSocketMessage(Message message) {
		User user = User.getInstance();
		boolean nameMentioned = message.getMessage().toLowerCase().contains(user.getUsername().toLowerCase());
		boolean oddMention = !message.getUser().equals(user.getUsername());

		// Needs option and keywords
		if (nameMentioned && oddMention && Message.isNotificationEnabled()) {
			Notifier.showNotification(
					message.getUser() + " mentioned you",
					message.getMessage(), true, getActivity(), null
			);
		}
		messageAdapter.addMessage(message);
		messageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSocketError(Exception e) {
		final Toast noNetworkToast = Toast.makeText(
				getActivity(), "Failed to connect to the Anime Twist chat.", Toast.LENGTH_LONG);
		if (e instanceof NullPointerException) {
			noNetworkToast.show();
		} else {
			e.printStackTrace();
		}
	}
}