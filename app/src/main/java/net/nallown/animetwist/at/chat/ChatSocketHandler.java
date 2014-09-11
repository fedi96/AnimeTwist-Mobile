package net.nallown.animetwist.at.chat;

import net.nallown.utils.States.SocketStates;
import net.nallown.utils.websocket.WebSocket;
import net.nallown.utils.websocket.WebSocketConnection;
import net.nallown.utils.websocket.WebSocketException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nasir on 28/08/2014.
 */
public class ChatSocketHandler implements WebSocket.WebSocketConnectionObserver {
	private SocketStates socketStates = null;
	private WebSocketConnection socketConnection;
	private URI ServerURI;
	private String WSS_HOST = "wss://animetwist.net:9000";

	public ChatSocketHandler(SocketStates socketStates) {
		this.socketStates = socketStates;

		reConnect();
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
		socketStates.onRawTextMessage(payload);
	}

	@Override
	public void onBinaryMessage(byte[] payload) {
		socketStates.onBinaryMessage(payload);
	}

	public void sendMessage(String msg) {
		socketConnection.sendTextMessage(msg);
	}

	public void reConnect() {
		if (socketConnection != null && socketConnection.isConnected()) {
			socketConnection.disconnect();
		}

		socketConnection = new WebSocketConnection();

		try {
			ServerURI = new URI(WSS_HOST);
			socketConnection.connect(ServerURI, this);
		} catch (URISyntaxException e) {
			this.socketStates.onError(e);
		} catch (WebSocketException e) {
			this.socketStates.onError(e);
		}
	}

	public WebSocketConnection getSocket() {
		return socketConnection;
	}

}
