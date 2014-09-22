package net.nallown.animetwist.at.chat;

import android.app.Activity;

import net.nallown.animetwist.at.User;
import net.nallown.utils.Network;
import net.nallown.utils.Notifier;
import net.nallown.utils.websocket.WebSocket;
import net.nallown.utils.websocket.WebSocketConnection;
import net.nallown.utils.websocket.WebSocketException;
import net.nallown.utils.websocket.WebSocketOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nasir on 28/08/2014.
 */
public class ChatSocket implements WebSocket.WebSocketConnectionObserver {
	private Activity activity;
	private User user;

	private SocketStates socketStates = null;
	private WebSocketOptions webSocketOptions;
	private WebSocketConnection socketConnection;
	private URI ServerURI;
	private String host = null;

	private boolean notificationEnabled = false;

	private boolean firstRun = true;
	private int messageCount = 0;

	public ChatSocket(String host, User user, Activity activity) {
		this.user = user;
		this.activity = activity;
		this.host = host;
	}


	public void setSocketStates(SocketStates socketStates) {
		this.socketStates = socketStates;
	}

	@Override
	public void onOpen() {
		socketStates.onSocketOpen();

		messageCount = 0;
	}

	@Override
	public void onClose(WebSocketCloseNotification code, String reason) {
		socketStates.onSocketClose(code, reason);
	}

	@Override
	public void onTextMessage(String payload) {
		JSONObject messageJsonObj = null;
		if (payload.equals("keep-alive")) {
			return;
		}
		if (messageCount >= 40 && firstRun) {
			firstRun = false;
		}

		try {
			messageJsonObj = new JSONObject(payload);
			String msg = messageJsonObj.optString("msg").toLowerCase();
			String msgUser = messageJsonObj.optString("username").toLowerCase();

			if (!messageJsonObj.has("auth")) {
				if ((!firstRun && messageCount >= 40) || firstRun) {
  	    	        // Needs option and keywords
					if (msg.contains(user.getUsername().toLowerCase())
						&& !msgUser.equals(user.getUsername().toLowerCase())
						&& notificationEnabled
					) {
						Notifier.showNotification(
							msgUser + " mentioned you",
							msg, true, activity
						);
					}

					socketStates.onSocketMessage(messageJsonObj);
				}

				if (messageCount != 40) {
					messageCount++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRawTextMessage(byte[] payload) {
//		socketStates.onRawTextMessage(payload);
	}

	@Override
	public void onBinaryMessage(byte[] payload) {
//		socketStates.onBinaryMessage(payload);
	}

	public void sendMessage(String msg) {
		socketConnection.sendTextMessage(msg);
	}

	public void sendUser(User user) {
		JSONObject authJson = new JSONObject();
		try {
			authJson.put("type", "auth");
			authJson.put("token", user.getSessionID());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		socketConnection.sendTextMessage(authJson.toString());
	}

	public WebSocketConnection getSocket() {
		return socketConnection;
	}


	public void connect(Activity activity) {
		if (!Network.isOnline(activity)) {
			return;
		}

		socketConnection = new WebSocketConnection();
		webSocketOptions = new WebSocketOptions();
		webSocketOptions.setSocketConnectTimeout((60 * 1000) * 5);
		webSocketOptions.setReconnectInterval((60 * 1000) * 5);

		try {
			ServerURI = new URI(host);
			socketConnection.connect(ServerURI, this, webSocketOptions);
		} catch (URISyntaxException e) {
			this.socketStates.onSocketError(e);
		} catch (WebSocketException e) {
			this.socketStates.onSocketError(e);
		}

		sendUser(user);
	}

	public void reConnect(Activity activity) {
		if (socketConnection != null && socketConnection.isConnected()) {
			socketConnection.disconnect();
		}

		connect(activity);
	}

	public void enableNotifications(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	public static interface SocketStates {
		public void onSocketOpen();

		public void onSocketClose(WebSocketCloseNotification code, String reason);

		public void onSocketMessage(JSONObject messageJsonObj);

//		public void onRawTextMessage(byte[] payload);
//
//		public void onBinaryMessage(byte[] payload);

		public void onSocketError(Exception e);
	}
}
