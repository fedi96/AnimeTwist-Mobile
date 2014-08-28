package net.nallown.animetwist.at.chat;

import net.nallown.animetwist.at.Listener.SocketListener;

import java.net.URI;
import java.net.URISyntaxException;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Nasir on 28/08/2014.
 */
public class ChatSocketHandler implements WebSocket.WebSocketConnectionObserver{
	private SocketListener socketListener = null;
	private WebSocketConnection socketConnection;
	private URI ServerURI;
	private String WSS_HOST = "wss://animetwist.net:9000";

	public ChatSocketHandler(SocketListener socketListener) {
		this.socketListener = socketListener;

		socketConnection = new WebSocketConnection();

		try {
			ServerURI = new URI(WSS_HOST);
			socketConnection.connect(ServerURI, this);
		} catch (URISyntaxException e) {
			this.socketListener.onError(e);
		} catch (WebSocketException e) {
			this.socketListener.onError(e);
		}
	}

	@Override
	public void onOpen() {
		socketListener.onOpen();
	}

	@Override
	public void onClose(WebSocketCloseNotification code, String reason) {
		socketListener.onClose(code, reason);
	}

	@Override
	public void onTextMessage(String payload) {
		socketListener.onTextMessage(payload);
	}

	@Override
	public void onRawTextMessage(byte[] payload) {
		socketListener.onRawTextMessage(payload);
	}

	@Override
	public void onBinaryMessage(byte[] payload) {
		socketListener.onBinaryMessage(payload);
	}

	public void sendMessage(String msg){
		socketConnection.sendTextMessage(msg);
	}
}
