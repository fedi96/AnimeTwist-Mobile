package net.nallown.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by root on 28/08/14.
 */
public class Network {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NON = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }

        return TYPE_NON;
    }

    public static String getStatusMessage(Context context) {
        int connection = Network.getConnectivityStatus(context);

        if (connection == Network.TYPE_WIFI) {
            return "Connected with WiFi";
        } else if (connection == Network.TYPE_MOBILE) {
            return "Connected with mobile data";
        }

        return "No network connection available";
    }

}
