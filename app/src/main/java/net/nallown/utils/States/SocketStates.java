package net.nallown.utils.States;

import net.nallown.utils.websocket.WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification;

/**
 * Created by Nasir on 28/08/2014.
 */
public abstract class SocketStates {
	public abstract void onOpen();

	public abstract void onClose(WebSocketCloseNotification code, String reason);

	public abstract void onTextMessage(String payload);

	public abstract void onRawTextMessage(byte[] payload);

	public abstract void onBinaryMessage(byte[] payload);

	public abstract void onError(Exception e);
}
