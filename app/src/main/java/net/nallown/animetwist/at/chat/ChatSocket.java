package net.nallown.animetwist.at.chat;

import android.app.Activity;

import net.nallown.animetwist.at.User;
import net.nallown.utils.Network;
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

	public ChatSocket(String host, Activity activity) {
		this.activity = activity;
		this.host = host;
	}

	public void setSocketStates(SocketStates socketStates) {
		this.socketStates = socketStates;
	}

	@Override
	public void onOpen() {
		socketStates.onSocketOpen();
	}

	@Override
	public void onClose(WebSocketCloseNotification code, String reason) {
		socketStates.onSocketClose(code, reason);
	}

	@Override
	public void onTextMessage(String payload) {
		if (payload.equals("keep-alive")) {
			return;
		}

		try {
			JSONObject messageJsonObj = new JSONObject(payload);
			if (!messageJsonObj.has("auth")) {
				Message message = Message.parseMessage(messageJsonObj);

				socketStates.onSocketMessage(message);
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

	public void sendTextMessage(String msg) {
		JSONObject msgJson = new JSONObject();
		try {
			msgJson.put("type", "msg");
			msgJson.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.sendMessage(msgJson.toString());
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
		webSocketOptions.setReconnectInterval((60 * 1000) * 6);

		try {
			ServerURI = new URI(host);
			socketConnection.connect(ServerURI, this, webSocketOptions);
		} catch (URISyntaxException e) {
			this.socketStates.onSocketError(e);
		} catch (WebSocketException e) {
			this.socketStates.onSocketError(e);
		} catch (NullPointerException e) {
			this.socketStates.onSocketError(e);
		}
	}

	public void reConnect(Activity activity) {
		if (socketConnection != null && socketConnection.isConnected()) {
			socketConnection.disconnect();
		}

		connect(activity);
	}

	public static interface SocketStates {
		public void onSocketOpen();

		public void onSocketClose(WebSocketCloseNotification code, String reason);

		public void onSocketMessage(Message message);

//		public void onRawTextMessage(byte[] payload);
//
//		public void onBinaryMessage(byte[] payload);

		public void onSocketError(Exception e);
	}
}
