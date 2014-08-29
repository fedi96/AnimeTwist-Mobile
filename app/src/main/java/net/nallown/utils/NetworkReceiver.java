package net.nallown.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by root on 28/08/14.
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static onNetworkChangeListener networkStates;

    @Override
    public void onReceive(Context context, Intent intent) {
        int connectionStatus = Network.getConnectivityStatus(context);
        String connectionMessage = Network.getStatusMessage(context);

	    if (networkStates != null)
	        networkStates.onNetworkChange(connectionStatus, connectionMessage);
    }

	public void setOnNetworkChangeListener(onNetworkChangeListener networkChangeListener){
		networkStates = networkChangeListener;
	}

	public static interface onNetworkChangeListener {
		public void onNetworkChange(int connection, String connectionMessage);
	}
}
