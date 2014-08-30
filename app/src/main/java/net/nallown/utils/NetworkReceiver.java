package net.nallown.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by root on 28/08/14.
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static onNetworkChangeListener networkStates;
	private static boolean connected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
		    if (connected != Network.isOnline(context, intent)) {
				networkStates.onNetworkChange(Network.isOnline(context, intent));
			    connected = Network.isOnline(context, intent);
		    }
	    }
    }

	public void setOnNetworkChangeListener(onNetworkChangeListener networkChangeListener){
		networkStates = networkChangeListener;
	}

	public static interface onNetworkChangeListener {
		public void onNetworkChange(boolean connected);
	}
}
