package net.nallown.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import net.nallown.utils.States.NetworkStates;

/**
 * Created by root on 28/08/14.
 */
public class NetworkReceiver extends BroadcastReceiver {

    NetworkStates networkStates = null;

    public NetworkReceiver() {
        super();
    }

    public NetworkReceiver(NetworkStates networkStates) {
        this.networkStates = networkStates;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int connectionStatus = Network.getConnectivityStatus(context);
        String connectionMessage = Network.getStatusMessage(context);

        this.networkStates.onConnectionChange(connectionStatus, connectionMessage);
    }
}
