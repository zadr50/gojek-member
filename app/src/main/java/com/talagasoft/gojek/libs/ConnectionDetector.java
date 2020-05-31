package com.talagasoft.gojek.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by compaq on 03/05/2016.
 */
public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivity.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = connectivity.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }

            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        }
        return false;
    }
}
