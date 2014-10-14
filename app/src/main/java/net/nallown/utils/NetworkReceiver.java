package net.nallown.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by root on 28/08/14.
 */
public class NetworkReceiver extends BroadcastReceiver {

	public static onNetworkChangeListener networkStates;

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean networkStatus = Network.isOnline(context);

		networkStates.onNetworkChange(networkStatus);
		Log.e("NetworkReceiver", "Network Changed");
	}

	public void setOnNetworkChangeListener(onNetworkChangeListener networkChangeListener) {
		networkStates = networkChangeListener;
	}

	public static interface onNetworkChangeListener {
		public void onNetworkChange(boolean connected);
	}
}
