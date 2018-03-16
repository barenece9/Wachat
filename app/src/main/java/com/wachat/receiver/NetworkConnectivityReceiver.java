package com.wachat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.wachat.services.ServiceXmppConnection;
import com.wachat.util.LogUtils;


public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(NetworkConnectivityReceiver.class);

    private static String lastActiveNetworkName = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            LogUtils.i(LOG_TAG,
                    "Connectivity Manager is null!");
            return;
        }

        for (NetworkInfo network : cm.getAllNetworkInfo()) {
            LogUtils.i(LOG_TAG,
                    "available=" + (network.isAvailable() ? 1 : 0)
                            + ", connected=" + (network.isConnected() ? 1 : 0)
                            + ", connectedOrConnecting="
                            + (network.isConnectedOrConnecting() ? 1 : 0)
                            + ", failover=" + (network.isFailover() ? 1 : 0)
                            + ", roaming=" + (network.isRoaming() ? 1 : 0)
                            + ", networkName=" + network.getTypeName());
        }

        boolean networkChanged = false;
        boolean connectedOrConnecting = false;
        boolean connected = false;

        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null /*&& ChatService.IsRunning*/) {
            String networkName = network.getTypeName();
            networkChanged = false;
            connectedOrConnecting = network.isConnectedOrConnecting();
            connected = network.isConnected();
            if (!networkName.equals(lastActiveNetworkName)) {
                lastActiveNetworkName = networkName;
                networkChanged = true;
            }
            LogUtils.i(LOG_TAG,
                    " name=" + network.getTypeName() + " changed="
                            + networkChanged + " connected=" + connected
                            + " connectedOrConnecting=" + connectedOrConnecting);

        }

        Intent svcIntent = new Intent(
                ServiceXmppConnection.ACTION_NETWORK_STATUS_CHANGED, null, context,
                ServiceXmppConnection.class);
      /*  svcIntent.putExtra("networkChanged", networkChanged);
        svcIntent.putExtra("connectedOrConnecting", connectedOrConnecting);
        svcIntent.putExtra("connected", connected);*/

        Bundle mBundle = new Bundle();
        mBundle.putBoolean("networkChanged", networkChanged);
        mBundle.putBoolean("connectedOrConnecting", connectedOrConnecting);
        mBundle.putBoolean("connected", connected);
        svcIntent.putExtras(mBundle);
        context.startService(svcIntent);

    }

    public static void setLastActiveNetworkName(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            lastActiveNetworkName = network.getTypeName();
        }
    }


}