package com.bestoffers.elnaqeltask.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SaraKhater on 1/13/19.
 */

public class CheckNetwork {

    Context context;
    private static CheckNetwork ourInstance = new CheckNetwork();

    public CheckNetwork(Context context) {
        this.context = context;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


    public static CheckNetwork getInstance() {
        return ourInstance;
    }

    private CheckNetwork() {
    }
}
