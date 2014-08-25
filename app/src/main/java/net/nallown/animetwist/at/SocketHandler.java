package net.nallown.animetwist.at;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nasir on 24/08/2014.
 */
public abstract class SocketHandler {
	private URI ATSocketUri;
	private WebSocketClient ATSocket;

	SocketHandler() {
		try {
			ATSocketUri = new URI("wss://animetwist.net:9000/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		ATSocket = new WebSocketClient(ATSocketUri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				onSocketOpen(serverHandshake);
			}

			@Override
			public void onMessage(String s) {
				onMessageReceive(s);
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				onSocketClose(i,s,b);
			}

			@Override
			public void onError(Exception e) {
				onSocketError(e);
			}
		};

		ATSocket.connect();
	}

	protected abstract void onSocketError(Exception e);
	protected abstract void onSocketClose(int i, String message, boolean b);
	protected abstract void onMessageReceive(String message);
	protected abstract void onSocketOpen(ServerHandshake serverHandshake);

	protected WebSocketClient getSocket() {
		return ATSocket;
	}
}
