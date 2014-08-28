package net.nallown.utils.States;

import net.nallown.utils.websocket.WebSocket;

/**
 * Created by Nasir on 28/08/2014.
 */
public abstract class SocketStates {
	public abstract void onOpen();
	public abstract void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String reason);
	public abstract void onTextMessage(String payload);
	public abstract void onRawTextMessage(byte[] payload);
	public abstract void onBinaryMessage(byte[] payload);
	public abstract void onError(Exception e);
}
