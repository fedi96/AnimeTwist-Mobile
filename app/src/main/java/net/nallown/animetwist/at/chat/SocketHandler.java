package net.nallown.animetwist.at.chat;

import android.os.AsyncTask;

import net.nallown.utils.websocket.WebSocket;
import net.nallown.utils.websocket.WebSocketConnection;
import net.nallown.utils.websocket.WebSocketException;
import net.nallown.utils.websocket.WebSocketOptions;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nasir on 28/08/2014.
 */
public class SocketHandler implements WebSocket.WebSocketConnectionObserver {
	private SocketStates socketStates = null;
	private WebSocketOptions webSocketOptions;
	private WebSocketConnection socketConnection;
	private URI ServerURI;
	private String host = null;

	public SocketHandler(String host) {
		this.host = host;
	}


	public void setSocketStates(SocketStates socketStates) {
		this.socketStates = socketStates;
	}

	@Override
	public void onOpen() {
		socketStates.onOpen();
	}

	@Override
	public void onClose(WebSocketCloseNotification code, String reason) {
		socketStates.onClose(code, reason);
	}

	@Override
	public void onTextMessage(String payload) {
		socketStates.onTextMessage(payload);
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

	public WebSocketConnection getSocket() {
		return socketConnection;
	}


	public void connect() {
		socketConnection = new WebSocketConnection();
		webSocketOptions = new WebSocketOptions();
		webSocketOptions.setSocketConnectTimeout((60 * 1000) * 45);
		webSocketOptions.setReconnectInterval((60 * 1000) * 15);

		try {
			ServerURI = new URI(host);
			socketConnection.connect(ServerURI, this, webSocketOptions);
		} catch (URISyntaxException e) {
			this.socketStates.onError(e);
		} catch (WebSocketException e) {
			this.socketStates.onError(e);
		}
	}

	public void reConnect() {
		if (socketConnection != null && socketConnection.isConnected()) {
			socketConnection.disconnect();
		}

		connect();
	}

	public static interface SocketStates {
		public void onOpen();

		public void onClose(WebSocketCloseNotification code, String reason);

		public void onTextMessage(String payload);

//		public void onRawTextMessage(byte[] payload);
//
//		public void onBinaryMessage(byte[] payload);

		public void onError(Exception e);
	}
}
