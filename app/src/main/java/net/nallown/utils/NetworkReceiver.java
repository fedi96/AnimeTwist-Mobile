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

	    if (networkStates != null) {
		    if (connectionStatus == Network.TYPE_NON) {
			    networkStates.onNetworkChange(false, connectionMessage);
		    } else {
			    networkStates.onNetworkChange(true, connectionMessage);
		    }

	    }
    }

	public void setOnNetworkChangeListener(onNetworkChangeListener networkChangeListener){
		networkStates = networkChangeListener;
	}

	public static interface onNetworkChangeListener {
		public void onNetworkChange(boolean connected, String connectionMessage);
	}
}
